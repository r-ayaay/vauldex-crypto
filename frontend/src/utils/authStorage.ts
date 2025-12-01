export function saveAuth(token: string, username: string, user_id: number) {
  localStorage.setItem('auth', JSON.stringify({ token, username, user_id }))
}

export function getAuth() {
  const data = localStorage.getItem('auth')
  return data ? JSON.parse(data) : null
}

export function clearAuth() {
  localStorage.removeItem('auth')
}

export function getToken() {
  return getAuth()?.token || null
}
