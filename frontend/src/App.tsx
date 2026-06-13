import { type ChangeEvent, type SubmitEvent, useRef, useState } from 'react'
import './App.css'

type ReviewStatus = 'NEEDS_REVIEW' | 'REVIEWED' | 'NOT_REQUIRED'

type AnalysisResponse = {
    analysisId: number
    analysisSource: string
    modelConfidence: number | null
    reviewStatus: string
    reviewReason: string | null
    status: string
    category: string | null
    priority: string | null
    customerIntent: string | null
    missingInformation: string[]
    validationMessages: string[]
}

type AnalyzeTicketRequest = {
    subject: string
    body: string
    customerId: string
}

type ReviewQueueItem = {
    analysisId: number
    subject: string
    customerId: string
    analysisSource: string
    modelConfidence: number | null
    reviewStatus: string
    reviewReason: string | null
    status: string
    category: string | null
    priority: string | null
    createdAt: string
    updatedAt: string
}

type ReviewQueueResponse = {
    items: ReviewQueueItem[]
    page: number
    size: number
    totalElements: number
    totalPages: number
}

type AnalysisDetailResponse = {
    analysisId: number
    subject: string
    body: string
    customerId: string
    analysisSource: string
    rawModelOutput: string | null
    modelConfidence: number | null
    reviewStatus: string
    reviewReason: string | null
    reviewedAt: string | null
    reviewedBy: string | null
    status: string
    category: string | null
    priority: string | null
    customerIntent: string | null
    missingInformation: string[]
    validationMessages: string[]
    createdAt: string
    updatedAt: string
}

type ApiErrorResponse = {
    timestamp?: string
    status?: number
    error?: string
    message?: string
    path?: string
    validationErrors?: {
        field: string
        message: string
    }[]
}

function App() {
    const [form, setForm] = useState<AnalyzeTicketRequest>({
        subject: 'Cannot login',
        body: 'I cannot access my account after password reset.',
        customerId: 'CUST-1001',
    })

    const [result, setResult] = useState<AnalysisResponse | null>(null)
    const [analyzeError, setAnalyzeError] = useState<string | null>(null)
    const [isSubmitting, setIsSubmitting] = useState(false)

    const [reviewQueue, setReviewQueue] = useState<ReviewQueueResponse | null>(null)
    const [reviewQueueError, setReviewQueueError] = useState<string | null>(null)
    const [isLoadingReviewQueue, setIsLoadingReviewQueue] = useState(false)
    const [reviewStatusFilter, setReviewStatusFilter] = useState<ReviewStatus>('NEEDS_REVIEW')
    const [reviewQueuePage, setReviewQueuePage] = useState(0)
    const [reviewQueueSize, setReviewQueueSize] = useState(10)

    const [analysisDetail, setAnalysisDetail] = useState<AnalysisDetailResponse | null>(null)
    const [analysisDetailError, setAnalysisDetailError] = useState<string | null>(null)
    const [isLoadingAnalysisDetail, setIsLoadingAnalysisDetail] = useState(false)

    const [reviewUpdateForm, setReviewUpdateForm] = useState({
        reviewStatus: 'NEEDS_REVIEW' as ReviewStatus,
        reviewReason: '',
        reviewedBy: '',
    })

    const [reviewUpdateError, setReviewUpdateError] = useState<string | null>(null)
    const [reviewUpdateSuccess, setReviewUpdateSuccess] = useState<string | null>(null)
    const [isUpdatingReview, setIsUpdatingReview] = useState(false)

    const detailSectionRef = useRef<HTMLElement | null>(null)

    async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
        event.preventDefault()

        setIsSubmitting(true)
        setAnalyzeError(null)
        setResult(null)

        try {
            const response = await fetch('/api/tickets/analyze', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(form),
            })

            if (!response.ok) {
                throw new Error(await readErrorMessage(response))
            }

            const data = (await response.json()) as AnalysisResponse
            setResult(data)
        } catch (err) {
            setAnalyzeError(err instanceof Error ? err.message : 'Unexpected error while analyzing ticket.')
        } finally {
            setIsSubmitting(false)
        }
    }

    async function loadReviewQueue(
        page = reviewQueuePage,
        size = reviewQueueSize,
        status = reviewStatusFilter,
    ) {
        setIsLoadingReviewQueue(true)
        setReviewQueueError(null)

        try {
            const query = new URLSearchParams({
                reviewStatus: status,
                page: String(page),
                size: String(size),
            })

            const response = await fetch(`/api/tickets/analyses?${query.toString()}`)

            if (!response.ok) {
                throw new Error(await readErrorMessage(response))
            }

            const data = (await response.json()) as ReviewQueueResponse
            setReviewQueue(data)
            setReviewQueuePage(data.page)
            setReviewQueueSize(data.size)
        } catch (err) {
            setReviewQueueError(
                err instanceof Error ? err.message : 'Unexpected error while loading review queue.',
            )
        } finally {
            setIsLoadingReviewQueue(false)
        }
    }

    async function loadAnalysisDetail(analysisId: number) {
        setIsLoadingAnalysisDetail(true)
        setAnalysisDetailError(null)
        setAnalysisDetail(null)

        try {
            const response = await fetch(`/api/tickets/analyses/${analysisId}`)

            if (!response.ok) {
                throw new Error(await readErrorMessage(response))
            }

            const data = (await response.json()) as AnalysisDetailResponse
            setAnalysisDetail(data)
            syncReviewUpdateForm(data)
            setReviewUpdateError(null)
            setReviewUpdateSuccess(null)

            window.setTimeout(() => {
                const detailSection = detailSectionRef.current

                if (!detailSection) {
                    return
                }

                const top = detailSection.getBoundingClientRect().top + window.scrollY - 16

                window.scrollTo({
                    top,
                    behavior: 'smooth',
                })
            }, 100)
        } catch (err) {
            setAnalysisDetailError(
                err instanceof Error ? err.message : 'Unexpected error while loading analysis detail.',
            )
        } finally {
            setIsLoadingAnalysisDetail(false)
        }
    }

    async function handleReviewUpdate(event: SubmitEvent<HTMLFormElement>) {
        event.preventDefault()

        if (!analysisDetail) {
            return
        }

        setIsUpdatingReview(true)
        setReviewUpdateError(null)
        setReviewUpdateSuccess(null)

        try {
            const response = await fetch(`/api/tickets/analyses/${analysisDetail.analysisId}/review`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    reviewStatus: reviewUpdateForm.reviewStatus,
                    reviewReason: reviewUpdateForm.reviewReason.trim() || null,
                    reviewedBy: reviewUpdateForm.reviewedBy.trim() || null,
                }),
            })

            if (!response.ok) {
                throw new Error(await readErrorMessage(response))
            }

            const updatedDetail = (await response.json()) as AnalysisDetailResponse

            setAnalysisDetail(updatedDetail)
            syncReviewUpdateForm(updatedDetail)
            setReviewUpdateSuccess('Review status updated.')

            if (reviewQueue) {
                await loadReviewQueue(reviewQueuePage, reviewQueueSize, reviewStatusFilter)
            }
        } catch (err) {
            setReviewUpdateError(
                err instanceof Error ? err.message : 'Unexpected error while updating review status.',
            )
        } finally {
            setIsUpdatingReview(false)
        }
    }

    async function handleReviewStatusChange(event: ChangeEvent<HTMLSelectElement>) {
        const nextStatus = event.target.value as ReviewStatus

        setReviewStatusFilter(nextStatus)
        setReviewQueuePage(0)
        clearAnalysisDetail()

        if (reviewQueue) {
            await loadReviewQueue(0, reviewQueueSize, nextStatus)
        }
    }

    async function handlePageSizeChange(event: ChangeEvent<HTMLSelectElement>) {
        const nextSize = Number(event.target.value)

        setReviewQueueSize(nextSize)
        setReviewQueuePage(0)
        clearAnalysisDetail()

        if (reviewQueue) {
            await loadReviewQueue(0, nextSize, reviewStatusFilter)
        }
    }
    async function goToPreviousPage() {
        const previousPage = Math.max(reviewQueuePage - 1, 0)
        clearAnalysisDetail()
        await loadReviewQueue(previousPage, reviewQueueSize, reviewStatusFilter)
    }

    async function goToNextPage() {
        if (!reviewQueue || reviewQueuePage >= reviewQueue.totalPages - 1) {
            return
        }

        clearAnalysisDetail()
        await loadReviewQueue(reviewQueuePage + 1, reviewQueueSize, reviewStatusFilter)
    }

    function clearAnalysisDetail() {
        setAnalysisDetail(null)
        setAnalysisDetailError(null)
        setReviewUpdateError(null)
        setReviewUpdateSuccess(null)
    }

    function scrollToTop() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth',
        })
    }

    function syncReviewUpdateForm(detail: AnalysisDetailResponse) {
        setReviewUpdateForm({
            reviewStatus: detail.reviewStatus as ReviewStatus,
            reviewReason: detail.reviewReason ?? '',
            reviewedBy: detail.reviewedBy ?? '',
        })
    }

    const canGoPrevious = reviewQueuePage > 0
    const canGoNext = reviewQueue ? reviewQueuePage < reviewQueue.totalPages - 1 : false

    return (
        <main className="app-shell">
            <section className="hero">
                <p className="eyebrow">AI Ticket Triage Service</p>
                <h1>Validated, persisted, reviewable ticket analysis</h1>
                <p>
                    Submit a support ticket and inspect the structured triage result returned by the backend.
                    The service can use deterministic logic or a local Ollama model depending on backend configuration.
                </p>
            </section>

            <section className="content-grid">
                <form className="card form-card" onSubmit={handleSubmit}>
                    <h2>Analyze Ticket</h2>

                    <label>
                        Subject
                        <input
                            value={form.subject}
                            onChange={(event) => setForm({ ...form, subject: event.target.value })}
                            required
                        />
                    </label>

                    <label>
                        Body
                        <textarea
                            value={form.body}
                            onChange={(event) => setForm({ ...form, body: event.target.value })}
                            rows={7}
                            required
                        />
                    </label>

                    <label>
                        Customer ID
                        <input
                            value={form.customerId}
                            onChange={(event) => setForm({ ...form, customerId: event.target.value })}
                            required
                        />
                    </label>

                    <button type="submit" disabled={isSubmitting}>
                        {isSubmitting ? 'Analyzing...' : 'Analyze Ticket'}
                    </button>
                </form>

                <section className="card result-card">
                    <h2>Analysis Result</h2>

                    {!result && !analyzeError && (
                        <p className="muted">Submit a ticket to see the persisted triage result.</p>
                    )}

                    {analyzeError && <pre className="error-box">{analyzeError}</pre>}

                    {result && (
                        <div className="result-grid">
                            <ResultItem label="Analysis ID" value={result.analysisId} />
                            <ResultItem label="Source" value={result.analysisSource} />
                            <ResultItem label="Status" value={result.status} />
                            <ResultItem label="Review Status" value={result.reviewStatus} />
                            <ResultItem label="Category" value={result.category ?? 'N/A'} />
                            <ResultItem label="Priority" value={result.priority ?? 'N/A'} />
                            <ResultItem
                                label="Confidence"
                                value={result.modelConfidence === null ? 'N/A' : result.modelConfidence}
                            />

                            <div className="wide">
                                <h3>Customer Intent</h3>
                                <p>{result.customerIntent ?? 'N/A'}</p>
                            </div>

                            <div className="wide">
                                <h3>Validation Messages</h3>
                                {result.validationMessages.length === 0 ? (
                                    <p className="muted">No validation messages.</p>
                                ) : (
                                    <ul>
                                        {result.validationMessages.map((message) => (
                                            <li key={message}>{message}</li>
                                        ))}
                                    </ul>
                                )}
                            </div>

                            <div className="wide">
                                <h3>Missing Information</h3>
                                {result.missingInformation.length === 0 ? (
                                    <p className="muted">None.</p>
                                ) : (
                                    <ul>
                                        {result.missingInformation.map((item) => (
                                            <li key={item}>{item}</li>
                                        ))}
                                    </ul>
                                )}
                            </div>
                        </div>
                    )}
                </section>
            </section>

            <section className="card review-queue-card">
                <div className="section-header">
                    <div>
                        <h2>Review Queue</h2>
                        <p className="muted">
                            Load saved analyses by review status and inspect records that need human review.
                        </p>
                    </div>

                    <button type="button" onClick={() => loadReviewQueue(0)} disabled={isLoadingReviewQueue}>
                        {isLoadingReviewQueue ? 'Loading...' : 'Load Queue'}
                    </button>
                </div>

                {reviewQueue && (
                    <div className="queue-controls">
                        <label>
                            Review status
                            <select value={reviewStatusFilter} onChange={handleReviewStatusChange}>
                                <option value="NEEDS_REVIEW">Needs Review</option>
                                <option value="REVIEWED">Reviewed</option>
                                <option value="NOT_REQUIRED">Not Required</option>
                            </select>
                        </label>

                        <label>
                            Records per page
                            <select value={reviewQueueSize} onChange={handlePageSizeChange}>
                                <option value={5}>5</option>
                                <option value={10}>10</option>
                                <option value={20}>20</option>
                                <option value={50}>50</option>
                            </select>
                        </label>
                    </div>
                )}

                {reviewQueueError && <pre className="error-box">{reviewQueueError}</pre>}

                {reviewQueue && (
                    <>
                        <div className="queue-summary-row">
                            <p className="queue-summary">
                                Showing {reviewQueue.items.length} of {reviewQueue.totalElements} item(s)
                            </p>

                            <div className="pagination-controls">
                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={goToPreviousPage}
                                    disabled={!canGoPrevious || isLoadingReviewQueue}
                                >
                                    Previous
                                </button>

                                <span>
                  Page {reviewQueue.totalPages === 0 ? 0 : reviewQueue.page + 1} of{' '}
                                    {reviewQueue.totalPages}
                </span>

                                <button
                                    type="button"
                                    className="secondary-button"
                                    onClick={goToNextPage}
                                    disabled={!canGoNext || isLoadingReviewQueue}
                                >
                                    Next
                                </button>
                            </div>
                        </div>

                        {reviewQueue.items.length === 0 ? (
                            <p className="muted">No items found for this review status.</p>
                        ) : (
                            <div className="queue-table-wrapper">
                                <table className="queue-table">
                                    <thead>
                                    <tr>
                                        <th>Subject</th>
                                        <th>Status</th>
                                        <th>Review</th>
                                        <th>Triage</th>
                                        <th>Confidence</th>
                                        <th>Reason</th>
                                        <th>Created</th>
                                        <th className="action-header" aria-label="Actions"></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {reviewQueue.items.map((item) => (
                                        <tr key={item.analysisId}>
                                            <td className="subject-cell">
                                                <strong>{item.subject}</strong>
                                                <span>{item.customerId}</span>
                                            </td>

                                            <td>{item.status}</td>

                                            <td>{item.reviewStatus}</td>

                                            <td>
                                                <strong>{item.category ?? 'N/A'}</strong>
                                                <span>{item.priority ?? 'N/A'}</span>
                                            </td>

                                            <td>{item.modelConfidence === null ? 'N/A' : item.modelConfidence}</td>

                                            <td className="reason-cell">{item.reviewReason ?? 'N/A'}</td>

                                            <td className="date-cell">{formatDateTime(item.createdAt)}</td>

                                            <td className="action-cell">
                                                <button
                                                    type="button"
                                                    className="icon-button"
                                                    onClick={() => loadAnalysisDetail(item.analysisId)}
                                                    disabled={isLoadingAnalysisDetail}
                                                    aria-label={`View analysis ${item.analysisId}`}
                                                    title="View details"
                                                >
                                                    <svg
                                                        aria-hidden="true"
                                                        viewBox="0 0 24 24"
                                                        width="18"
                                                        height="18"
                                                        fill="none"
                                                        stroke="currentColor"
                                                        strokeWidth="2"
                                                        strokeLinecap="round"
                                                        strokeLinejoin="round"
                                                    >
                                                        <path d="M2 12s3.5-7 10-7 10 7 10 7-3.5 7-10 7-10-7-10-7z" />
                                                        <circle cx="12" cy="12" r="3" />
                                                    </svg>
                                                </button>
                                            </td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </>
                )}
            </section>
            <section ref={detailSectionRef} className="card detail-card">
                <div className="section-header">
                    <div>
                        <h2>Analysis Detail</h2>
                        <p className="muted">
                            Inspect the original ticket, raw model output, parsed result, validation messages, and review metadata.
                        </p>
                    </div>
                </div>

                {!analysisDetail && !analysisDetailError && !isLoadingAnalysisDetail && (
                    <p className="muted">Select a row from the review queue to inspect the saved analysis.</p>
                )}

                {isLoadingAnalysisDetail && <p className="muted">Loading analysis detail...</p>}

                {analysisDetailError && <pre className="error-box">{analysisDetailError}</pre>}

                {analysisDetail && (
                    <div className="detail-grid">
                        <ResultItem label="Analysis ID" value={analysisDetail.analysisId} />
                        <ResultItem label="Source" value={analysisDetail.analysisSource} />
                        <ResultItem label="Status" value={analysisDetail.status} />
                        <ResultItem label="Review Status" value={analysisDetail.reviewStatus} />
                        <ResultItem label="Category" value={analysisDetail.category ?? 'N/A'} />
                        <ResultItem label="Priority" value={analysisDetail.priority ?? 'N/A'} />
                        <ResultItem
                            label="Confidence"
                            value={analysisDetail.modelConfidence === null ? 'N/A' : analysisDetail.modelConfidence}
                        />
                        <ResultItem label="Reviewed By" value={analysisDetail.reviewedBy ?? 'N/A'} />
                        <ResultItem
                            label="Reviewed At"
                            value={analysisDetail.reviewedAt ? formatDateTime(analysisDetail.reviewedAt) : 'N/A'}
                        />
                        <ResultItem label="Created" value={formatDateTime(analysisDetail.createdAt)} />
                        <ResultItem label="Updated" value={formatDateTime(analysisDetail.updatedAt)} />

                        <div className="detail-section review-update-section">
                            <h3>Update Review Status</h3>

                            <form className="review-update-form" onSubmit={handleReviewUpdate}>
                                <label>
                                    Review status
                                    <select
                                        value={reviewUpdateForm.reviewStatus}
                                        onChange={(event) =>
                                            setReviewUpdateForm({
                                                ...reviewUpdateForm,
                                                reviewStatus: event.target.value as ReviewStatus,
                                            })
                                        }
                                    >
                                        <option value="NEEDS_REVIEW">Needs Review</option>
                                        <option value="REVIEWED">Reviewed</option>
                                        <option value="NOT_REQUIRED">Not Required</option>
                                    </select>
                                </label>

                                <label>
                                    Review reason
                                    <textarea
                                        value={reviewUpdateForm.reviewReason}
                                        onChange={(event) =>
                                            setReviewUpdateForm({
                                                ...reviewUpdateForm,
                                                reviewReason: event.target.value,
                                            })
                                        }
                                        rows={3}
                                        placeholder="Add a short review note."
                                    />
                                </label>

                                <label>
                                    Reviewed by
                                    <input
                                        value={reviewUpdateForm.reviewedBy}
                                        onChange={(event) =>
                                            setReviewUpdateForm({
                                                ...reviewUpdateForm,
                                                reviewedBy: event.target.value,
                                            })
                                        }
                                        placeholder="local-reviewer"
                                    />
                                </label>

                                <button type="submit" disabled={isUpdatingReview}>
                                    {isUpdatingReview ? 'Updating...' : 'Update Review'}
                                </button>
                            </form>

                            {reviewUpdateSuccess && <p className="success-message">{reviewUpdateSuccess}</p>}
                            {reviewUpdateError && <pre className="error-box">{reviewUpdateError}</pre>}
                        </div>

                        <div className="detail-section">
                            <h3>Original Ticket</h3>
                            <p><strong>Subject:</strong> {analysisDetail.subject}</p>
                            <p><strong>Customer ID:</strong> {analysisDetail.customerId}</p>
                            <pre className="text-block">{analysisDetail.body}</pre>
                        </div>

                        <div className="detail-section">
                            <h3>Customer Intent</h3>
                            <p>{analysisDetail.customerIntent ?? 'N/A'}</p>
                        </div>

                        <div className="detail-section">
                            <h3>Review Reason</h3>
                            <p>{analysisDetail.reviewReason ?? 'N/A'}</p>
                        </div>

                        <div className="detail-section">
                            <h3>Validation Messages</h3>
                            {analysisDetail.validationMessages.length === 0 ? (
                                <p className="muted">No validation messages.</p>
                            ) : (
                                <ul>
                                    {analysisDetail.validationMessages.map((message) => (
                                        <li key={message}>{message}</li>
                                    ))}
                                </ul>
                            )}
                        </div>

                        <div className="detail-section">
                            <h3>Missing Information</h3>
                            {analysisDetail.missingInformation.length === 0 ? (
                                <p className="muted">None.</p>
                            ) : (
                                <ul>
                                    {analysisDetail.missingInformation.map((item) => (
                                        <li key={item}>{item}</li>
                                    ))}
                                </ul>
                            )}
                        </div>

                        <div className="detail-section">
                            <h3>Raw Model Output</h3>
                            <pre className="text-block">{analysisDetail.rawModelOutput ?? 'N/A'}</pre>
                        </div>
                    </div>
                )}
            </section>
            <button
                type="button"
                className="go-top-button"
                onClick={scrollToTop}
                aria-label="Go to top"
                title="Go to top"
            >
                <svg
                    aria-hidden="true"
                    viewBox="0 0 24 24"
                    width="20"
                    height="20"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2.2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                >
                    <path d="M12 19V5" />
                    <path d="M5 12l7-7 7 7" />
                </svg>
            </button>
        </main>
    )
}

function ResultItem({ label, value }: { label: string | number; value: string | number }) {
    return (
        <div className="result-item">
            <span>{label}</span>
            <strong>{value}</strong>
        </div>
    )
}

async function readErrorMessage(response: Response): Promise<string> {
    const text = await response.text()

    if (!text) {
        return `Request failed with status ${response.status}`
    }

    try {
        const apiError = JSON.parse(text) as ApiErrorResponse

        if (apiError.validationErrors && apiError.validationErrors.length > 0) {
            return apiError.validationErrors
                .map((validationError) => `${validationError.field}: ${validationError.message}`)
                .join('\n')
        }

        return apiError.message || text
    } catch {
        return text
    }
}

function formatDateTime(value: string): string {
    return new Intl.DateTimeFormat(undefined, {
        dateStyle: 'medium',
        timeStyle: 'short',
    }).format(new Date(value))
}

export default App