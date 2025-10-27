import React from 'react'
import {Layout, Menu} from 'antd'
import {useLocation, useNavigate} from 'react-router-dom'
import {
    ApartmentOutlined,
    ClusterOutlined,
    DashboardOutlined,
    FileTextOutlined,
    SettingOutlined,
} from '@ant-design/icons'

const {Sider} = Layout

const Sidebar: React.FC = () => {
    const navigate = useNavigate()
    const location = useLocation()

    const menuItems = [
        {
            key: '/',
            icon: <DashboardOutlined/>,
            label: 'Dashboard',
        },
        {
            key: '/workflows',
            icon: <ApartmentOutlined/>,
            label: 'Workflows',
            children: [
                {
                    key: '/workflows',
                    label: 'List',
                },
                {
                    key: '/workflows/designer',
                    label: 'Designer',
                },
            ],
        },
        {
            key: '/kafka',
            icon: <ClusterOutlined/>,
            label: 'Kafka Monitor',
        },
        {
            key: '/logs',
            icon: <FileTextOutlined/>,
            label: 'System Logs',
        },
        {
            key: '/settings',
            icon: <SettingOutlined/>,
            label: 'Settings',
        },
    ]

    const handleMenuClick = ({key}: { key: string }) => {
        navigate(key)
    }

    return (
        <Sider width={250} style={{background: '#fff'}}>
            <div style={{padding: '16px', textAlign: 'center', borderBottom: '1px solid #f0f0f0'}}>
                <h2 style={{margin: 0, color: '#1890ff'}}>Kafka Microservices</h2>
            </div>
            <Menu
                mode="inline"
                selectedKeys={[location.pathname]}
                items={menuItems}
                onClick={handleMenuClick}
                style={{borderRight: 0}}
            />
        </Sider>
    )
}

export default Sidebar
