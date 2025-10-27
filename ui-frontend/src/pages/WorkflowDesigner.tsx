import React, {useCallback} from 'react'
import ReactFlow, {
    addEdge,
    Background,
    Connection,
    Controls,
    MiniMap,
    Node,
    useEdgesState,
    useNodesState,
} from 'reactflow'
import 'reactflow/dist/style.css'
import {Button, Card, message, Space, Typography} from 'antd'
import {PlayCircleOutlined, SaveOutlined} from '@ant-design/icons'
import {useWorkflowStore} from '../stores/workflowStore'

const {Title} = Typography

const WorkflowDesigner: React.FC = () => {
    const [nodes, setNodes, onNodesChange] = useNodesState([])
    const [edges, setEdges, onEdgesChange] = useEdgesState([])
    const {saveWorkflow, executeWorkflow} = useWorkflowStore()

    const onConnect = useCallback(
        (params: Connection) => setEdges((eds) => addEdge(params, eds)),
        [setEdges]
    )

    const addNode = (type: string) => {
        const newNode: Node = {
            id: `${type}-${Date.now()}`,
            type: 'default',
            position: {x: Math.random() * 400, y: Math.random() * 400},
            data: {label: `${type} Node`},
        }
        setNodes((nds) => [...nds, newNode])
    }

    const handleSave = async () => {
        try {
            const workflowData = {
                nodes,
                edges,
                name: `Workflow-${Date.now()}`,
                version: '1.0',
            }

            await saveWorkflow(workflowData)
            message.success('Workflow saved successfully')
        } catch (error) {
            message.error('Failed to save workflow')
        }
    }

    const handleExecute = async () => {
        try {
            const workflowData = {
                nodes,
                edges,
                name: `Workflow-${Date.now()}`,
                version: '1.0',
            }

            await executeWorkflow(workflowData)
            message.success('Workflow executed successfully')
        } catch (error) {
            message.error('Failed to execute workflow')
        }
    }

    return (
        <div style={{height: '100vh', display: 'flex', flexDirection: 'column'}}>
            <Card style={{marginBottom: 16}}>
                <Space>
                    <Title level={4} style={{margin: 0}}>Workflow Designer</Title>
                    <Button type="primary" icon={<SaveOutlined/>} onClick={handleSave}>
                        Save Workflow
                    </Button>
                    <Button type="default" icon={<PlayCircleOutlined/>} onClick={handleExecute}>
                        Execute Workflow
                    </Button>
                </Space>
            </Card>

            <Card style={{flex: 1}}>
                <div style={{height: '100%', display: 'flex'}}>
                    <div style={{width: '200px', padding: '16px', borderRight: '1px solid #f0f0f0'}}>
                        <Title level={5}>Node Types</Title>
                        <Space direction="vertical" style={{width: '100%'}}>
                            <Button block onClick={() => addNode('Service Call')}>
                                Service Call
                            </Button>
                            <Button block onClick={() => addNode('Schedule Task')}>
                                Schedule Task
                            </Button>
                            <Button block onClick={() => addNode('Data Processing')}>
                                Data Processing
                            </Button>
                            <Button block onClick={() => addNode('Notification')}>
                                Notification
                            </Button>
                            <Button block onClick={() => addNode('Condition')}>
                                Condition
                            </Button>
                        </Space>
                    </div>

                    <div style={{flex: 1, height: '100%'}}>
                        <ReactFlow
                            nodes={nodes}
                            edges={edges}
                            onNodesChange={onNodesChange}
                            onEdgesChange={onEdgesChange}
                            onConnect={onConnect}
                            fitView
                        >
                            <Controls/>
                            <MiniMap/>
                            <Background variant="dots" gap={12} size={1}/>
                        </ReactFlow>
                    </div>
                </div>
            </Card>
        </div>
    )
}

export default WorkflowDesigner
