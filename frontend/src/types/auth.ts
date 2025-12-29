export interface LoginRequest {
  email: string
  senha: string
}

export interface AuthResponse {
  token: string
}
