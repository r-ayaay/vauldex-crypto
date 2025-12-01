import { ref } from 'vue'

export interface Holding {
  id: number
  symbol: string
  amount: number
  currentPrice: number
  totalValue: number
}

export function usePortfolioSocket(token: string) {
  const portfolio = ref<Holding[]>([])
  let ws: WebSocket | null = null

  const connect = () => {
    const safeToken = encodeURIComponent(token)
    ws = new WebSocket(`ws://localhost:8080/ws/portfolio?token=${safeToken}`)

    ws.onopen = () => {
      console.log('Connected to portfolio WebSocket')
    }

    ws.onmessage = (event: MessageEvent) => {
      console.log('WS portfolio update:', event.data)
      portfolio.value = JSON.parse(event.data)
    }

    ws.onclose = () => {
      console.log('WebSocket closed. Reconnecting in 5s...')
      setTimeout(connect, 5000) // <-- continue updating same portfolio ref
    }

    ws.onopen = () => {
      console.log('%c[WS] Connected', 'color: lightgreen; font-weight: bold;')
    }

    ws.onerror = (e) => {
      console.log('%c[WS] Error:', 'color: orange; font-weight: bold;', e)
    }

    ws.onclose = (e) => {
      console.log('%c[WS] Closed:', 'color: red; font-weight: bold;', e)
      setTimeout(connect, 5000)
    }

    ws.onmessage = (event: MessageEvent) => {
      console.log('%c[WS] Raw Message:', 'color: cyan; font-weight: bold;', event.data)

      try {
        const parsed = JSON.parse(event.data)
        console.log('%c[WS] Parsed JSON:', 'color: yellow; font-weight: bold;', parsed)

        portfolio.value = parsed
      } catch (err) {
        console.log('%c[WS] JSON Parse Error:', 'color: red; font-weight: bold;', err)
      }
    }
  }

  connect()

  return { portfolio }
}
