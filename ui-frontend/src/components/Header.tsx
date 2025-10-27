import React from 'react'
import {Avatar, Button, Dropdown, Layout, Space} from 'antd'
import {LogoutOutlined, SettingOutlined, UserOutlined} from '@ant-design/icons'
import {useAuthStore} from '../stores/authStore'

const {Header: AntHeader} = Layout

const Header: React.FC = () => {
    const {user, logout} = useAuthStore()

    const userMenuItems = [
        {
            key: 'profile',
            icon: <UserOutlined/>,
            label: 'Profile',
        },
        {
            key: 'settings',
            icon: <SettingOutlined/>,
            label: 'Settings',
        },
        {
            type: 'divider' as const,
        },
        {
            key: 'logout',
            icon: <LogoutOutlined/>,
            label: 'Logout',
            onClick: logout,
        },
    ]

    return (
        <AntHeader style={{background: '#fff', padding: '0 24px', borderBottom: '1px solid #f0f0f0'}}>
            <div style={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', height: '100%'}}>
                <div>
                    <h1 style={{margin: 0, fontSize: '20px', fontWeight: 'bold'}}>Microservices Dashboard</h1>
                </div>
                <Space>
                    <Dropdown menu={{items: userMenuItems}} placement="bottomRight">
                        <Button type="text" style={{display: 'flex', alignItems: 'center', gap: '8px'}}>
                            <Avatar icon={<UserOutlined/>} size="small"/>
                            {user?.username || 'Guest'}
                        </Button>
                    </Dropdown>
                </Space>
            </div>
        </AntHeader>
    )
}

export default Header
