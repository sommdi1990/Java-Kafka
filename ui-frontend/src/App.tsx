import React from 'react'
import {Route, Routes} from 'react-router-dom'
import {Layout} from 'antd'
import Sidebar from './components/Sidebar'
import Header from './components/Header'
import Dashboard from './pages/Dashboard'
import WorkflowDesigner from './pages/WorkflowDesigner'
import WorkflowList from './pages/WorkflowList'
import KafkaMonitor from './pages/KafkaMonitor'
import SystemLogs from './pages/SystemLogs'
import Settings from './pages/Settings'

const {Content} = Layout

const App: React.FC = () => {
    return (
        <Layout style={{minHeight: '100vh'}}>
            <Sidebar/>
            <Layout>
                <Header/>
                <Content style={{margin: '24px 16px', padding: 24, background: '#fff'}}>
                    <Routes>
                        <Route path="/" element={<Dashboard/>}/>
                        <Route path="/workflows" element={<WorkflowList/>}/>
                        <Route path="/workflows/designer" element={<WorkflowDesigner/>}/>
                        <Route path="/workflows/designer/:id" element={<WorkflowDesigner/>}/>
                        <Route path="/kafka" element={<KafkaMonitor/>}/>
                        <Route path="/logs" element={<SystemLogs/>}/>
                        <Route path="/settings" element={<Settings/>}/>
                    </Routes>
                </Content>
            </Layout>
        </Layout>
    )
}

export default App
