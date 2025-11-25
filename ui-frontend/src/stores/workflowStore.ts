import {create} from 'zustand'

interface Workflow {
    id: string
    name: string
    version: string
    definition: any
    status: string
    createdAt: string
    updatedAt: string
}

interface WorkflowState {
    workflows: Workflow[]
    currentWorkflow: Workflow | null
    isLoading: boolean

    setWorkflows: (workflows: Workflow[]) => void
    setCurrentWorkflow: (workflow: Workflow | null) => void
    setLoading: (loading: boolean) => void

    saveWorkflow: (workflowData: any) => Promise<void>
    executeWorkflow: (workflowData: any) => Promise<void>
    deleteWorkflow: (id: string) => Promise<void>
}

export const useWorkflowStore = create<WorkflowState>((set, _get) => ({
    workflows: [],
    currentWorkflow: null,
    isLoading: false,

    setWorkflows: (workflows: Workflow[]) => {
        set({workflows})
    },

    setCurrentWorkflow: (workflow: Workflow | null) => {
        set({currentWorkflow: workflow})
    },

    setLoading: (loading: boolean) => {
        set({isLoading: loading})
    },

    saveWorkflow: async (workflowData: any) => {
        set({isLoading: true})
        try {
            const response = await fetch('/api/workflow/save', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(workflowData),
            })

            if (response.ok) {
                const data = await response.json()
                set((state) => ({
                    workflows: [...state.workflows, data.data],
                }))
            } else {
                throw new Error('Failed to save workflow')
            }
        } catch (error) {
            throw error
        } finally {
            set({isLoading: false})
        }
    },

    executeWorkflow: async (workflowData: any) => {
        set({isLoading: true})
        try {
            const response = await fetch('/api/workflow/execute', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(workflowData),
            })

            if (!response.ok) {
                throw new Error('Failed to execute workflow')
            }
        } catch (error) {
            throw error
        } finally {
            set({isLoading: false})
        }
    },

    deleteWorkflow: async (id: string) => {
        set({isLoading: true})
        try {
            const response = await fetch(`/api/workflow/${id}`, {
                method: 'DELETE',
            })

            if (response.ok) {
                set((state) => ({
                    workflows: state.workflows.filter((w) => w.id !== id),
                }))
            } else {
                throw new Error('Failed to delete workflow')
            }
        } catch (error) {
            throw error
        } finally {
            set({isLoading: false})
        }
    },
}))
