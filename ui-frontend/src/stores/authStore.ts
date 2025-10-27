import {create} from 'zustand'
import {persist} from 'zustand/middleware'

interface User {
    id: number
    username: string
    email: string
    roles: string[]
}

interface AuthState {
    user: User | null
    token: string | null
    isAuthenticated: boolean
    login: (username: string, password: string) => Promise<void>
    logout: () => void
    setUser: (user: User) => void
    setToken: (token: string) => void
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set, get) => ({
            user: null,
            token: null,
            isAuthenticated: false,

            login: async (username: string, password: string) => {
                try {
                    const response = await fetch('/api/auth/login', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({username, password}),
                    })

                    if (response.ok) {
                        const data = await response.json()
                        set({
                            user: data.data.user,
                            token: data.data.token,
                            isAuthenticated: true,
                        })
                    } else {
                        throw new Error('Login failed')
                    }
                } catch (error) {
                    throw error
                }
            },

            logout: () => {
                set({
                    user: null,
                    token: null,
                    isAuthenticated: false,
                })
            },

            setUser: (user: User) => {
                set({user})
            },

            setToken: (token: string) => {
                set({token, isAuthenticated: true})
            },
        }),
        {
            name: 'auth-storage',
        }
    )
)
