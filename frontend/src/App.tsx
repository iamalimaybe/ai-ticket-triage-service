import { type SubmitEvent, useState } from 'react'
import './App.css'

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

function App() {
    const [form, setForm] = useState<AnalyzeTicketRequest>({
        subject: 'Cannot login',
        body: 'I cannot access my account after password reset.',
        customerId: 'CUST-1001',
    })

    const [result, setResult] = useState<AnalysisResponse | null>(null)
    const [error, setError] = useState<string | null>(null)
    const [isSubmitting, setIsSubmitting] = useState(false)

    async function handleSubmit(event: SubmitEvent<HTMLFormElement>) {
        event.preventDefault()

        setIsSubmitting(true)
        setError(null)
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
                const errorBody = await response.text()
                throw new Error(errorBody || `Request failed with status ${response.status}`)
            }

            const data = (await response.json()) as AnalysisResponse
            setResult(data)
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Unexpected error while analyzing ticket.')
        } finally {
            setIsSubmitting(false)
        }
    }

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

                    {!result && !error && (
                        <p className="muted">Submit a ticket to see the persisted triage result.</p>
                    )}

                    {error && (
                        <pre className="error-box">{error}</pre>
                    )}

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
        </main>
    )
}

function ResultItem({ label, value }: { label: string; value: string | number }) {
    return (
        <div className="result-item">
            <span>{label}</span>
            <strong>{value}</strong>
        </div>
    )
}

export default App