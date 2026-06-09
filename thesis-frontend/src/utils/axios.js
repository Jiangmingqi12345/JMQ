import axios from 'axios'
import { useUserStore } from '../stores/user'

// 创建axios实例
const service = axios.create({
  baseURL: 'http://localhost:8080', // 后端API地址
  timeout: 15000, // 请求超时时间
  withCredentials: true // 允许携带Cookie，用于Session认证
  // 移除全局Content-Type头，让axios自动处理
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 注意：后端使用Session认证，不需要添加Authorization头
    // 只需要确保withCredentials: true，允许携带Cookie
    
    // 如果请求数据是FormData类型（用于文件上传），则删除Content-Type头，让浏览器自动设置
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }
    return config
  },
  (error) => {
    // 请求错误处理
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    // 直接返回response.data，即后端的Result对象
    // 前端代码需要自己处理Result对象的结构
    return response.data
  },
  (error) => {
    // 响应错误处理
    let message = '网络错误'
    
    if (error.response) {
      // HTTP状态码处理
      switch (error.response.status) {
        case 401:
          message = '未授权，请重新登录'
          // 清除用户信息，但不在axios文件中跳转路由
          const userStore = useUserStore()
          userStore.logout()
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          // 直接使用后端返回的错误信息
          message = error.response.data.message || error.response.data || `请求失败(${error.response.status})`
      }
    } else if (error.request) {
      // 请求已发出，但没有收到响应
      message = '服务器无响应'
    }
    
    console.error('Axios错误:', error)
    return Promise.reject(new Error(message))
  }
)

export default service
