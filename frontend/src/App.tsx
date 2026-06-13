import { type ChangeEvent, type SubmitEvent, useState } from 'react'
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

    async function handleReviewStatusChange(event: ChangeEvent<HTMLSelectElement>) {
        const nextStatus = event.target.value as ReviewStatus
        setReviewStatusFilter(nextStatus)
        setReviewQueuePage(0)

        if (reviewQueue) {
            await loadReviewQueue(0, reviewQueueSize, nextStatus)
        }
    }

    async function handlePageSizeChange(event: ChangeEvent<HTMLSelectElement>) {
        const nextSize = Number(event.target.value)
        setReviewQueueSize(nextSize)
        setReviewQueuePage(0)

        if (reviewQueue) {
            await loadReviewQueue(0, nextSize, reviewStatusFilter)
        }
    }

    async function goToPreviousPage() {
        const previousPage = Math.max(reviewQueuePage - 1, 0)
        await loadReviewQueue(previousPage, reviewQueueSize, reviewStatusFilter)
    }

    async function goToNextPage() {
        if (!reviewQueue || reviewQueuePage >= reviewQueue.totalPages - 1) {
            return
        }

        await loadReviewQueue(reviewQueuePage + 1, reviewQueueSize, reviewStatusFilter)
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
                                        <th>ID</th>
                                        <th>Subject</th>
                                        <th>Status</th>
                                        <th>Review Status</th>
                                        <th>Category</th>
                                        <th>Priority</th>
                                        <th>Confidence</th>
                                        <th>Reason</th>
                                        <th>Created</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {reviewQueue.items.map((item) => (
                                        <tr key={item.analysisId}>
                                            <td>{item.analysisId}</td>
                                            <td className="text-cell">
                                                <strong>{item.subject}</strong>
                                                <span>{item.customerId}</span>
                                            </td>
                                            <td>{item.status}</td>
                                            <td>{item.reviewStatus}</td>
                                            <td>{item.category ?? 'N/A'}</td>
                                            <td>{item.priority ?? 'N/A'}</td>
                                            <td>{item.modelConfidence === null ? 'N/A' : item.modelConfidence}</td>
                                            <td className="reason-cell">{item.reviewReason ?? 'N/A'}</td>
                                            <td>{formatDateTime(item.createdAt)}</td>
                                        </tr>
                                    ))}
                                    </tbody>
                                </table>
                            </div>
                        )}
                    </>
                )}
            </section>
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