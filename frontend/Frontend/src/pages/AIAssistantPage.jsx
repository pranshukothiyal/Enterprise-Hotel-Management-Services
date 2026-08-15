import { useEffect, useRef, useState } from 'react'
import {
  Bot,
  Building2,
  CheckCircle2,
  Database,
  Send,
  Sparkles,
  Trash2,
  Wifi
} from 'lucide-react'
import toast from 'react-hot-toast'
import { aiApi } from '../api/services'
import {
  Badge,
  Button,
  Card,
  Input,
  Modal,
  PageHeader,
  Select
} from '../components/ui'
import { errorMessage } from '../lib/utils'

const suggestions = [
  'Which rooms are currently available?',
  'Recommend a hotel for a family stay.',
  'Show affordable available rooms.',
  'What live hotel information can you access?'
]

export default function AIAssistantPage() {
  const [method, setMethod] = useState('chat')

  const [messages, setMessages] = useState([
    {
      role: 'assistant',
      content:
        'Hello! I am the StayOps AI Assistant. I use live hotel and room tools and will not invent prices or availability.'
    }
  ])

  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const [health, setHealth] = useState(null)
  const [debug, setDebug] = useState(null)

  const bottom = useRef(null)

  useEffect(() => {
    bottom.current?.scrollIntoView({
      behavior: 'smooth'
    })
  }, [messages, loading])

  const send = async (text = input, method = 'chat') => {
    const message = text.trim()

    if (!message || loading) return

    setMessages((m) => [
      ...m,
      {
        role: 'user',
        content: message
      }
    ])

    setInput('')
    setLoading(true)

    try {
      const response =
        method === 'ask'
          ? await aiApi.ask(message)
          : await aiApi.chat(message)

      setMessages((m) => [
        ...m,
        {
          role: 'assistant',
          content: response.answer || String(response),
          model: response.model
        }
      ])
    } catch (error) {
      const msg = errorMessage(error)

      toast.error(msg)

      setMessages((m) => [
        ...m,
        {
          role: 'assistant',
          content: `I could not reach the AI service: ${msg}`,
          error: true
        }
      ])
    } finally {
      setLoading(false)
    }
  }


  return (
    <div className="flex flex-col h-[calc(100vh-7rem)]">
      <PageHeader
        eyebrow="Intelligence"
        title="AI Assistant"
        action={
          <div className="flex items-center gap-3">
            <Button
              variant="secondary"
              onClick={() => setMessages(messages.slice(0, 1))}
            >
              Clear
            </Button>
          </div>
        }
      />

      <Card className="flex flex-col flex-1 p-6 mt-4 overflow-hidden">
        <div className="mb-4 shrink-0">
          <h2 className="text-lg font-semibold text-slate-900 dark:text-white">
            StayOps Assistant
          </h2>
          <p className="text-sm text-slate-500">Live tool-enabled assistant</p>
        </div>

        <div className="flex-1 space-y-4 overflow-y-auto pr-2 min-h-0">
          {messages.map((message, index) => (
            <div
              key={index}
              className={`flex ${
                message.role === 'user' ? 'justify-end' : 'justify-start'
              }`}
            >
              <div
                className={`max-w-[85%] rounded-2xl px-4 py-3 text-sm leading-7 ${
                  message.role === 'user'
                    ? 'rounded-br-md bg-slate-950 text-white dark:bg-cyan-400 dark:text-slate-950'
                    : message.error
                    ? 'rounded-bl-md border border-rose-200 bg-rose-50 text-rose-800 dark:border-rose-900 dark:bg-rose-950 dark:text-rose-200'
                    : 'rounded-bl-md border border-slate-200 bg-white text-slate-700 dark:border-slate-800 dark:bg-slate-900 dark:text-slate-200'
                }`}
              >
                <div>{message.content}</div>
                {message.model && (
                  <div className="mt-1 text-xs opacity-75">
                    Model: {message.model}
                  </div>
                )}
              </div>
            </div>
          ))}

          {loading && (
            <div className="flex justify-start">
              <div className="rounded-2xl rounded-bl-md border border-slate-200 bg-white px-4 py-3 text-sm text-slate-500 dark:border-slate-800 dark:bg-slate-900">
                Thinking with live hotel data…
              </div>
            </div>
          )}

          <div ref={bottom} />
        </div>

        <div className="mt-4 shrink-0">
          <div className="flex flex-wrap gap-2 mb-3">
            {suggestions.map((s) => (
              <button
                key={s}
                onClick={() => send(s)}
                className="shrink-0 rounded-full border border-slate-200 px-3 py-1.5 text-xs font-medium text-slate-500 transition hover:border-cyan-400 hover:text-cyan-600 dark:border-slate-700"
              >
                {s}
              </button>
            ))}
          </div>

          <form
            onSubmit={(e) => {
              e.preventDefault()
              send(input, method)
            }}
            className="flex gap-3"
          >
           

            <Input
              value={input}
              onChange={(e) => setInput(e.target.value)}
              placeholder="Ask about hotels, rooms, prices, capacity or availability…"
            />

            <Button type="submit">
              <Send className="h-4 w-4 mr-1" />
              Send
            </Button>
          </form>
        </div>
      </Card>

      <Modal
        open={Boolean(debug)}
        onClose={() => setDebug(null)}
        title={`AI debug: ${debug?.type}`}
        size="xl"
        footer={
          <Button variant="secondary" onClick={() => setDebug(null)}>
            Close
          </Button>
        }
      >
        <pre className="max-h-[400px] overflow-auto text-xs bg-slate-900 text-slate-100 p-4 rounded-lg">
          {JSON.stringify(debug?.data, null, 2)}
        </pre>
      </Modal>
    </div>
  )
}