import {create} from 'zustand'

export interface Workflow {
    id: number
    name: string
    description?: string
    version: string
    definitionJson?: string
    status: 'DRAFT' | 'ACTIVE' | 'INACTIVE' | 'ARCHIVED'
    createdBy?: string
    createdAt: string
    updatedAt?: string
}

interface WorkflowState {
    workflows: Workflow[]
    currentWorkflow: Workflow | null
    isLoading: boolean

    setWorkflows: (workflows: Workflow[]) => void
    setCurrentWorkflow: (workflow: Workflow | null) => void
    setLoading: (loading: boolean) => void

    loadWorkflows: () => Promise<void>
    loadWorkflowById: (id: number) => Promise<Workflow>
    saveWorkflow: (payload: {
        name: string;
        version: string;
        definitionJson: string;
        description?: string
    }) => Promise<Workflow>
    updateWorkflow: (id: number, payload: {
        name: string;
        version: string;
        definitionJson: string;
        description?: string
    }) => Promise<Workflow>
    executeWorkflowByName: (workflowName: string, context?: any) => Promise<void>
    updateWorkflowStatus: (id: number, status: Workflow['status']) => Promise<void>
}

export const useWorkflowStore = create<WorkflowState>((set, get) => ({
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

    loadWorkflows: async () => {
        set({isLoading: true})
        try {
            const response = await fetch('/api/workflow/definitions')
            if (!response.ok) {
                throw new Error('Failed to load workflows')
            }
            const body = await response.json()
            set({workflows: body.data as Workflow[]})
        } finally {
            set({isLoading: false})
        }
    },

    loadWorkflowById: async (id: number) => {
        set({isLoading: true})
        try {
            const response = await fetch(`/api/workflow/definitions/${id}`)
            if (!response.ok) {
                throw new Error('Failed to load workflow')
            }
            const body = await response.json()
            const wf = body.data as Workflow
            set({currentWorkflow: wf})
            return wf
        } finally {
            set({isLoading: false})
        }
    },

    saveWorkflow: async (payload) => {
        set({isLoading: true})
        try {
            const response = await fetch('/api/workflow/definitions', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    ...payload,
                    status: 'ACTIVE',
                }),
            })

            if (!response.ok) {
                throw new Error('Failed to save workflow')
            }

            const body = await response.json()
            const created = body.data as Workflow

            set({
                workflows: [...get().workflows, created],
                currentWorkflow: created,
            })

            return created
        } finally {
            set({isLoading: false})
        }
    },

    updateWorkflow: async (id, payload) => {
        set({isLoading: true})
        try {
            const response = await fetch(`/api/workflow/definitions/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload),
            })

            if (!response.ok) {
                throw new Error('Failed to update workflow')
            }

            const body = await response.json()
            const updated = body.data as Workflow

            set((state) => ({
                workflows: state.workflows.map((w) => (w.id === id ? updated : w)),
                currentWorkflow: updated,
            }))

            return updated
        } finally {
            set({isLoading: false})
        }
    },

    executeWorkflowByName: async (workflowName: string, context: any = {}) => {
        set({isLoading: true})
        try {
            const response = await fetch(`/api/workflow/execute-by-name/${encodeURIComponent(workflowName)}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(context),
            })

            if (!response.ok) {
                throw new Error('Failed to execute workflow')
            }
        } finally {
            set({isLoading: false})
        }
    },

    updateWorkflowStatus: async (id: number, status: Workflow['status']) => {
        set({isLoading: true})
        try {
            const response = await fetch(`/api/workflow/definitions/${id}/status?status=${status}`, {
                method: 'PATCH',
            })

            if (!response.ok) {
                throw new Error('Failed to update workflow status')
            }

            const body = await response.json()
            const updated = body.data as Workflow

            set((state) => ({
                workflows: state.workflows.map((w) => (w.id === id ? updated : w)),
                currentWorkflow: state.currentWorkflow?.id === id ? updated : state.currentWorkflow,
            }))
        } finally {
            set({isLoading: false})
        }
    },
}))
