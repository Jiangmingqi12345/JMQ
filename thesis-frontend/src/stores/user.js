import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    userInfo: null,
    token: null,
    isLoggedIn: false
  }),
  
  getters: {
    getUserInfo: (state) => state.userInfo,
    getToken: (state) => state.token,
    getIsLoggedIn: (state) => state.isLoggedIn,
    getRole: (state) => state.userInfo?.role
  },
  
  actions: {
    // 登录
    login(userInfo, token) {
      this.userInfo = userInfo
      this.token = token
      this.isLoggedIn = true
    },
    
    // 登出
    logout() {
      this.userInfo = null
      this.token = null
      this.isLoggedIn = false
      // 清除本地存储
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    },
    
    // 从本地存储恢复用户信息
    restoreUserInfo() {
      const user = localStorage.getItem('user')
      const token = localStorage.getItem('token')
      if (user && token) {
        this.userInfo = JSON.parse(user)
        this.token = token
        this.isLoggedIn = true
      }
    },
    
    // 更新用户信息
    updateUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      localStorage.setItem('user', JSON.stringify(this.userInfo))
    }
  }
})
