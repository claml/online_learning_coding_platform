import api from './axios'

export type UserRole = 'ADMIN' | 'TEACHER' | 'STUDENT' | string
export type BindingStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'UNBOUND' | string

export interface UserProfile {
  id?: number
  username: string
  avatarUrl?: string
  role: UserRole
  bindingStatus?: BindingStatus
  boundStatus?: boolean
  studentId?: string
  teacherId?: string
  identifier?: string
}

export interface UpdateProfileRequest {
  username: string
  avatarUrl?: string
}

export interface BindIdentifierRequest {
  identifier: string
  studentId?: string
  teacherId?: string
}

export interface BindIdentifierResponse {
  status: BindingStatus
  identifier?: string
  studentId?: string
  teacherId?: string
}

export interface UpdatePasswordRequest {
  oldPassword: string
  newPassword: string
}

export interface MessageResponse {
  message?: string
}

export const getCurrentUser = () => api.get<UserProfile>('/users/me')

export const updateCurrentUser = (payload: UpdateProfileRequest) =>
  api.put<UserProfile>('/users/me', payload)

export const bindIdentifier = (payload: BindIdentifierRequest) =>
  api.post<BindIdentifierResponse>('/users/me/bind', payload)

export const updatePassword = (payload: UpdatePasswordRequest) =>
  api.put<MessageResponse>('/users/me/password', payload)
