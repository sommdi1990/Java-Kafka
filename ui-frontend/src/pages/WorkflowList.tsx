import React, {useEffect, useState} from 'react'
import {Button, Card, Descriptions, message, Modal, Space, Table, Tag, Typography} from 'antd'
import type {ColumnsType} from 'antd/es/table'
import {PlayCircleOutlined, ReloadOutlined} from '@ant-design/icons'
import {useNavigate} from 'react-router-dom'
import {useWorkflowStore, Workflow} from '../stores/workflowStore'

const {Title, Text} = Typography

interface WorkflowInstance {
    id: number
    workflowDefinitionId: number
    instanceName: string
    status: 'RUNNING' | 'COMPLETED' | 'FAILED' | 'PAUSED' | 'CANCELLED'
    currentStep?: string
    startedBy?: string
    startedAt: string
    completedAt?: string
    executionTimeMs?: number
    errorMessage?: string
}

const statusColorMap: Record<Workflow['status'], string> = {
    DRAFT: 'default',
    ACTIVE: 'green',
    INACTIVE: 'orange',
    ARCHIVED: 'red',
}

const instanceStatusColorMap: Record<WorkflowInstance['status'], string> = {
    RUNNING: 'processing',
    COMPLETED: 'green',
    FAILED: 'red',
    PAUSED: 'orange',
    CANCELLED: 'default',
}

const WorkflowList: React.FC = () => {
    const {workflows, loadWorkflows, executeWorkflowByName, updateWorkflowStatus, isLoading} = useWorkflowStore()
    const navigate = useNavigate()
    const [instances, setInstances] = useState<WorkflowInstance[]>([])
    const [instancesLoading, setInstancesLoading] = useState(false)
    const [selectedInstance, setSelectedInstance] = useState<WorkflowInstance | null>(null)
    const [instanceModalVisible, setInstanceModalVisible] = useState(false)

    const fetchInstances = async () => {
        setInstancesLoading(true)
        try {
            const response = await fetch('/api/workflow/instances')
            if (!response.ok) {
                throw new Error('Failed to load workflow instances')
            }
            const body = await response.json()
            setInstances(body.data as WorkflowInstance[])
        } catch (e) {
            message.error('خطا در دریافت لاگ اجرای ورک‌فلوها')
        } finally {
            setInstancesLoading(false)
        }
    }

    useEffect(() => {
        loadWorkflows().catch(() => {
            message.error('خطا در دریافت لیست ورک‌فلوها')
        })
        fetchInstances().catch(() => {
            // خطا قبلاً هندل می‌شود
        })
    }, [])

    const handleExecute = async (record: Workflow) => {
        try {
            await executeWorkflowByName(record.name, {
                context: {triggeredFrom: 'workflows-list'},
            })
            message.success(`Workflow "${record.name}" با موفقیت اجرا شد`)
            fetchInstances().catch(() => undefined)
        } catch (e) {
            message.error('اجرای ورک‌فلو با خطا مواجه شد')
        }
    }

    const handleToggleStatus = async (record: Workflow) => {
        const nextStatus: Workflow['status'] =
            record.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'

        try {
            await updateWorkflowStatus(record.id, nextStatus)
            message.success('وضعیت ورک‌فلو به‌روزرسانی شد')
        } catch (e) {
            message.error('به‌روزرسانی وضعیت با خطا مواجه شد')
        }
    }

    const columns: ColumnsType<Workflow> = [
        {
            title: 'ID',
            dataIndex: 'id',
            width: 80,
        },
        {
            title: 'Name',
            dataIndex: 'name',
            render: (text, record) => (
                <Button type="link" onClick={() => navigate(`/workflows/designer/${record.id}`)}>
                    {text}
                </Button>
            ),
        },
        {
            title: 'Version',
            dataIndex: 'version',
            width: 100,
        },
        {
            title: 'Status',
            dataIndex: 'status',
            width: 120,
            render: (status: Workflow['status']) => (
                <Tag color={statusColorMap[status]}>{status}</Tag>
            ),
        },
        {
            title: 'Created At',
            dataIndex: 'createdAt',
            width: 200,
        },
        {
            title: 'Actions',
            key: 'actions',
            width: 220,
            render: (_value, record) => (
                <Space>
                    <Button
                        size="small"
                        icon={<PlayCircleOutlined/>}
                        onClick={() => handleExecute(record)}
                    >
                        Run
                    </Button>
                    <Button
                        size="small"
                        onClick={() => handleToggleStatus(record)}
                    >
                        {record.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
                    </Button>
                </Space>
            ),
        },
    ]

    const latestInstances = instances.slice().sort(
        (a, b) => new Date(b.startedAt).getTime() - new Date(a.startedAt).getTime()
    ).slice(0, 10)

    const instanceColumns: ColumnsType<WorkflowInstance> = [
        {
            title: 'ID',
            dataIndex: 'id',
            width: 80,
        },
        {
            title: 'Workflow ID',
            dataIndex: 'workflowDefinitionId',
            width: 110,
        },
        {
            title: 'Instance Name',
            dataIndex: 'instanceName',
        },
        {
            title: 'Status',
            dataIndex: 'status',
            width: 120,
            render: (status: WorkflowInstance['status']) => (
                <Tag color={instanceStatusColorMap[status]}>{status}</Tag>
            ),
        },
        {
            title: 'Started At',
            dataIndex: 'startedAt',
            width: 200,
        },
        {
            title: 'Completed At',
            dataIndex: 'completedAt',
            width: 200,
        },
    ]

    return (
        <Space direction="vertical" style={{width: '100%'}} size="large">
            <Card
                title={<Title level={4} style={{margin: 0}}>Workflows</Title>}
                extra={
                    <Button
                        icon={<ReloadOutlined/>}
                        onClick={() => {
                            loadWorkflows().catch(() => message.error('خطا در دریافت لیست ورک‌فلوها'))
                            fetchInstances().catch(() => undefined)
                        }}
                    >
                        Refresh
                    </Button>
                }
            >
                <Table<Workflow>
                    rowKey="id"
                    columns={columns}
                    dataSource={workflows}
                    loading={isLoading}
                    pagination={{pageSize: 10}}
                />
            </Card>

            <Card
                title={<Title level={5} style={{margin: 0}}>Latest Workflow Executions</Title>}
                extra={
                    <Text type="secondary">
                        نمایش ۱۰ اجرای اخیر
                    </Text>
                }
            >
                <Table<WorkflowInstance>
                    rowKey="id"
                    columns={instanceColumns}
                    dataSource={latestInstances}
                    loading={instancesLoading}
                    pagination={false}
                    size="small"
                    onRow={(record) => ({
                        onClick: () => {
                            setSelectedInstance(record)
                            setInstanceModalVisible(true)
                        },
                        style: {cursor: 'pointer'},
                    })}
                />
            </Card>

            <Modal
                open={instanceModalVisible}
                onCancel={() => setInstanceModalVisible(false)}
                footer={null}
                width={700}
                title="Workflow Instance Details"
            >
                {selectedInstance && (
                    <Descriptions column={1} bordered size="small">
                        <Descriptions.Item label="ID">{selectedInstance.id}</Descriptions.Item>
                        <Descriptions.Item label="Workflow Definition ID">
                            {selectedInstance.workflowDefinitionId}
                        </Descriptions.Item>
                        <Descriptions.Item label="Instance Name">
                            {selectedInstance.instanceName}
                        </Descriptions.Item>
                        <Descriptions.Item label="Status">
                            <Tag color={instanceStatusColorMap[selectedInstance.status]}>
                                {selectedInstance.status}
                            </Tag>
                        </Descriptions.Item>
                        <Descriptions.Item label="Current Step">
                            {selectedInstance.currentStep ?? '-'}
                        </Descriptions.Item>
                        <Descriptions.Item label="Started By">
                            {selectedInstance.startedBy ?? '-'}
                        </Descriptions.Item>
                        <Descriptions.Item label="Started At">
                            {selectedInstance.startedAt}
                        </Descriptions.Item>
                        <Descriptions.Item label="Completed At">
                            {selectedInstance.completedAt ?? '-'}
                        </Descriptions.Item>
                        <Descriptions.Item label="Execution Time (ms)">
                            {selectedInstance.executionTimeMs ?? '-'}
                        </Descriptions.Item>
                        <Descriptions.Item label="Error Message">
                            {selectedInstance.errorMessage ?? '-'}
                        </Descriptions.Item>
                    </Descriptions>
                )}
            </Modal>
        </Space>
    )
}

export default WorkflowList