import api from './axios'

export interface PostSummary {
  id: number
  title: string
  content: string
  createdAt: string
  likeCount: number
  commentCount: number
}

export type GetMyPostsResponse = PostSummary[]

export const getMyPosts = () => api.get<GetMyPostsResponse>('/community/posts/me')
