import React, {useCallback, useEffect, useState} from 'react'
import ReactFlow, {
    addEdge,
    Background,
    BackgroundVariant,
    Connection,
    Controls,
    MiniMap,
    Node,
    useEdgesState,
    useNodesState,
} from 'reactflow'
import 'reactflow/dist/style.css'
import {Button, Card, Form, Input, InputNumber, message, Select, Space, Typography} from 'antd'
import {PlayCircleOutlined, SaveOutlined} from '@ant-design/icons'
import {useParams} from 'react-router-dom'
import {useWorkflowStore} from '../stores/workflowStore'

const {Title} = Typography

type NodeTypeKey =
    | 'service_call'
    | 'schedule_task'
    | 'data_processing'
    | 'notification'
    | 'condition'
    | 'graphql_call'
    | 'delay'
    | 'external_event'

const nodeTypeLabels: { label: string; type: NodeTypeKey }[] = [
    {label: 'Service Call', type: 'service_call'},
    {label: 'Schedule Task', type: 'schedule_task'},
    {label: 'Data Processing', type: 'data_processing'},
    {label: 'Notification', type: 'notification'},
    {label: 'Condition', type: 'condition'},
    {label: 'GraphQL Call', type: 'graphql_call'},
    {label: 'Delay', type: 'delay'},
    {label: 'External Event', type: 'external_event'},
]

const WorkflowDesigner: React.FC = () => {
    const params = useParams<{ id?: string }>()
    const [nodes, setNodes, onNodesChange] = useNodesState([])
    const [edges, setEdges, onEdgesChange] = useEdgesState([])
    const {currentWorkflow, saveWorkflow, updateWorkflow, executeWorkflowByName, loadWorkflowById} = useWorkflowStore()

    const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null)
    const [workflowName, setWorkflowName] = useState<string>(`Workflow-${Date.now()}`)
    const [form] = Form.useForm()

    const onConnect = useCallback(
        (params: Connection) => setEdges((eds) => addEdge(params, eds)),
        [setEdges]
    )

    const addNode = (label: string, type: NodeTypeKey) => {
        const newNode: Node = {
            id: `${type}-${Date.now()}`,
            type: 'default',
            position: {x: Math.random() * 400, y: Math.random() * 400},
            data: {
                label: label,
                stepType: type,
            },
        }
        setNodes((nds) => [...nds, newNode])
    }

    const onNodeClick = (_: any, node: Node) => {
        setSelectedNodeId(node.id)
        const stepType: NodeTypeKey | undefined = node.data?.stepType
        if (!stepType) {
            form.resetFields()
            return
        }

        form.setFieldsValue({
            label: node.data?.label,
            stepType,
            service: node.data?.service,
            endpoint: node.data?.endpoint,
            task: node.data?.task,
            cron: node.data?.cron,
            processingType: node.data?.processingType,
            batchSize: node.data?.batchSize,
            notificationType: node.data?.notificationType,
            message: node.data?.message,
            condition: node.data?.condition,
            delayMs: node.data?.delayMs,
            eventType: node.data?.eventType,
        })
    }

    const updateSelectedNode = (values: any) => {
        if (!selectedNodeId) return
        setNodes((nds) =>
            nds.map((n) =>
                n.id === selectedNodeId
                    ? {
                        ...n,
                        data: {
                            ...n.data,
                            ...values,
                        },
                    }
                    : n
            )
        )
    }

    const buildDefinitionJson = () => {
        const steps = nodes.map((node) => {
            const stepType: NodeTypeKey | undefined = (node.data as any)?.stepType
            const base: any = {
                name: node.id,
                type: stepType,
            }

            switch (stepType) {
                case 'service_call':
                    base.service = (node.data as any)?.service ?? 'cbi-service'
                    base.endpoint = (node.data as any)?.endpoint ?? '/api/cbi/rest/1'
                    break
                case 'schedule_task':
                    base.task = (node.data as any)?.task ?? 'data-processing'
                    base.cron = (node.data as any)?.cron ?? '0 0 12 * * ?'
                    break
                case 'data_processing':
                    base.processingType = (node.data as any)?.processingType ?? 'DEFAULT'
                    base.batchSize = (node.data as any)?.batchSize ?? 100
                    break
                case 'notification':
                    base.notificationType = (node.data as any)?.notificationType ?? 'email'
                    base.message = (node.data as any)?.message ?? 'Workflow completed successfully'
                    break
                case 'condition':
                    base.condition = (node.data as any)?.condition ?? 'context.flag == true'
                    base.trueStep = (node.data as any)?.trueStep ?? ''
                    base.falseStep = (node.data as any)?.falseStep ?? ''
                    break
                case 'graphql_call':
                    base.service = (node.data as any)?.service ?? 'cbi-service'
                    base.query = (node.data as any)?.query ?? 'query { countries { code name } }'
                    break
                case 'delay':
                    base.delayMs = (node.data as any)?.delayMs ?? 1000
                    break
                case 'external_event':
                    base.eventType = (node.data as any)?.eventType ?? 'WORKFLOW_EVENT'
                    break
                default:
                    break
            }

            return base
        })

        return JSON.stringify({
            name: workflowName,
            version: '1.0',
            steps,
        })
    }

    useEffect(() => {
        const load = async () => {
            if (!params.id) return
            const idNum = Number(params.id)
            if (Number.isNaN(idNum)) return

            try {
                const wf = await loadWorkflowById(idNum)
                setWorkflowName(wf.name)

                if (wf.definitionJson) {
                    const parsed = JSON.parse(wf.definitionJson) as { steps?: any[] }
                    const steps = parsed.steps ?? []
                    const loadedNodes: Node[] = steps.map((step, index) => {
                        const stepType = step.type as NodeTypeKey
                        return {
                            id: step.name ?? `${stepType}-${index}`,
                            type: 'default',
                            position: {x: (index % 4) * 200, y: Math.floor(index / 4) * 120},
                            data: {
                                label: step.name ?? `${stepType} Node`,
                                stepType,
                                ...step,
                            },
                        }
                    })

                    const loadedEdges = steps
                        .map((step, index) => {
                            const sourceId = step.name ?? `${step.type}-${index}`
                            const next = steps[index + 1]
                            if (!next) return null
                            const targetId = next.name ?? `${next.type}-${index + 1}`
                            return {
                                id: `${sourceId}-${targetId}`,
                                source: sourceId,
                                target: targetId,
                            }
                        })
                        .filter(Boolean) as any[]

                    setNodes(loadedNodes)
                    setEdges(loadedEdges)
                }
            } catch (e) {
                message.error('خطا در لود ورک‌فلو برای ویرایش')
            }
        }

        load().catch(() => undefined)
    }, [params.id])

    const handleSave = async () => {
        try {
            const definitionJson = buildDefinitionJson()
            if (currentWorkflow?.id) {
                await updateWorkflow(currentWorkflow.id, {
                    name: workflowName,
                    version: currentWorkflow.version ?? '1.0',
                    definitionJson,
                })
                message.success('Workflow updated successfully')
            } else {
                await saveWorkflow({
                    name: workflowName,
                    version: '1.0',
                    definitionJson,
                })
                message.success('Workflow saved successfully')
            }
        } catch (error) {
            message.error('Failed to save workflow')
        }
    }

    const handleExecute = async () => {
        try {
            await executeWorkflowByName(workflowName, {
                context: {
                    triggeredFrom: 'ui-designer',
                },
            })
            message.success('Workflow executed successfully')
        } catch (error) {
            message.error('Failed to execute workflow')
        }
    }

    return (
        <div style={{height: '100vh', display: 'flex', flexDirection: 'column'}}>
            <Card style={{marginBottom: 16}}>
                <Space align="center" style={{width: '100%', justifyContent: 'space-between'}}>
                    <Space>
                        <Title level={4} style={{margin: 0}}>Workflow Designer</Title>
                        <Input
                            style={{width: 260}}
                            value={workflowName}
                            onChange={(e) => setWorkflowName(e.target.value)}
                            placeholder="Workflow name"
                        />
                    </Space>
                    <Space>
                        <Button type="primary" icon={<SaveOutlined/>} onClick={handleSave}>
                            Save Workflow
                        </Button>
                        <Button type="default" icon={<PlayCircleOutlined/>} onClick={handleExecute}>
                            Execute Workflow
                        </Button>
                    </Space>
                </Space>
            </Card>

            <Card style={{flex: 1}}>
                <div style={{height: '100%', display: 'flex'}}>
                    <div style={{width: '220px', padding: '16px', borderRight: '1px solid #f0f0f0'}}>
                        <Title level={5}>Node Types</Title>
                        <Space direction="vertical" style={{width: '100%'}}>
                            {nodeTypeLabels.map((item) => (
                                <Button
                                    key={item.type}
                                    block
                                    onClick={() => addNode(item.label, item.type)}
                                >
                                    {item.label}
                                </Button>
                            ))}
                        </Space>
                    </div>

                    <div style={{flex: 1, height: '100%'}}>
                        <ReactFlow
                            nodes={nodes}
                            edges={edges}
                            onNodesChange={onNodesChange}
                            onEdgesChange={onEdgesChange}
                            onConnect={onConnect}
                            onNodeClick={onNodeClick}
                            fitView
                        >
                            <Controls/>
                            <MiniMap/>
                            <Background variant={BackgroundVariant.Dots} gap={12} size={1}/>
                        </ReactFlow>
                    </div>

                    <div style={{width: 300, padding: '16px', borderLeft: '1px solid #f0f0f0'}}>
                        <Title level={5}>Node Properties</Title>
                        {selectedNodeId ? (
                            <Form
                                layout="vertical"
                                form={form}
                                onValuesChange={(_, values) => updateSelectedNode(values)}
                            >
                                <Form.Item label="Label" name="label">
                                    <Input/>
                                </Form.Item>
                                <Form.Item label="Step Type" name="stepType">
                                    <Select disabled>
                                        {nodeTypeLabels.map((nt) => (
                                            <Select.Option key={nt.type} value={nt.type}>
                                                {nt.label}
                                            </Select.Option>
                                        ))}
                                    </Select>
                                </Form.Item>

                                <Form.Item noStyle shouldUpdate>
                                    {() => {
                                        const stepType = form.getFieldValue('stepType') as NodeTypeKey
                                        if (stepType === 'service_call' || stepType === 'graphql_call') {
                                            return (
                                                <>
                                                    <Form.Item label="Service" name="service">
                                                        <Input placeholder="cbi-service"/>
                                                    </Form.Item>
                                                    {stepType === 'service_call' ? (
                                                        <Form.Item label="Endpoint" name="endpoint">
                                                            <Input placeholder="/api/cbi/rest/1"/>
                                                        </Form.Item>
                                                    ) : (
                                                        <Form.Item label="GraphQL Query" name="query">
                                                            <Input.TextArea rows={4}/>
                                                        </Form.Item>
                                                    )}
                                                </>
                                            )
                                        }
                                        if (stepType === 'schedule_task') {
                                            return (
                                                <>
                                                    <Form.Item label="Task Name" name="task">
                                                        <Input placeholder="data-processing"/>
                                                    </Form.Item>
                                                    <Form.Item label="Cron" name="cron">
                                                        <Input placeholder="0 0 12 * * ?"/>
                                                    </Form.Item>
                                                </>
                                            )
                                        }
                                        if (stepType === 'data_processing') {
                                            return (
                                                <>
                                                    <Form.Item label="Processing Type" name="processingType">
                                                        <Input placeholder="DEFAULT"/>
                                                    </Form.Item>
                                                    <Form.Item label="Batch Size" name="batchSize">
                                                        <InputNumber min={1} style={{width: '100%'}}/>
                                                    </Form.Item>
                                                </>
                                            )
                                        }
                                        if (stepType === 'notification') {
                                            return (
                                                <>
                                                    <Form.Item label="Notification Type" name="notificationType">
                                                        <Input placeholder="email"/>
                                                    </Form.Item>
                                                    <Form.Item label="Message" name="message">
                                                        <Input.TextArea rows={3}/>
                                                    </Form.Item>
                                                </>
                                            )
                                        }
                                        if (stepType === 'condition') {
                                            return (
                                                <>
                                                    <Form.Item label="Condition" name="condition">
                                                        <Input placeholder="context.amount > 1000"/>
                                                    </Form.Item>
                                                    <Form.Item label="True Step Name" name="trueStep">
                                                        <Input/>
                                                    </Form.Item>
                                                    <Form.Item label="False Step Name" name="falseStep">
                                                        <Input/>
                                                    </Form.Item>
                                                </>
                                            )
                                        }
                                        if (stepType === 'delay') {
                                            return (
                                                <Form.Item label="Delay (ms)" name="delayMs">
                                                    <InputNumber min={0} style={{width: '100%'}}/>
                                                </Form.Item>
                                            )
                                        }
                                        if (stepType === 'external_event') {
                                            return (
                                                <Form.Item label="Event Type" name="eventType">
                                                    <Input placeholder="WORKFLOW_COMPLETED"/>
                                                </Form.Item>
                                            )
                                        }
                                        return null
                                    }}
                                </Form.Item>
                            </Form>
                        ) : (
                            <Typography.Text type="secondary">
                                یک نود را برای ویرایش انتخاب کنید.
                            </Typography.Text>
                        )}
                    </div>
                </div>
            </Card>
        </div>
    )
}

export default WorkflowDesigner
