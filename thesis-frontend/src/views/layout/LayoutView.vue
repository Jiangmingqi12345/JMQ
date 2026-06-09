<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="asideWidth" class="layout-aside" :class="{ 'is-collapsed': isCollapsed }">
      <div class="aside-header">
        <h3 class="logo">{{ isCollapsed ? '毕设' : '毕业设计管理系统' }}</h3>
        <el-button
          type="text"
          @click="isCollapsed = !isCollapsed"
          class="collapse-btn"
          :icon="isCollapsed ? 'Expand' : 'Fold'"
        />
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="layout-menu"
        :collapse="isCollapsed"
        :collapse-transition="false"
        router
        unique-opened
        text-color="#fff"
        active-text-color="#ffd04b"
        background-color="#304156"
      >
        <!-- 首页 -->
        <el-menu-item index="/home">
          <template #icon>
            <el-icon><House /></el-icon>
          </template>
          <template #title>首页</template>
        </el-menu-item>
        
        <!-- 用户管理（仅管理员可见） -->
        <el-sub-menu v-if="userRole === 'ADMIN'" index="user">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/user/list">用户列表</el-menu-item>
          <el-menu-item index="/user/add">添加用户</el-menu-item>
        </el-sub-menu>
        
        <!-- 项目管理 -->
        <el-sub-menu index="project">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>项目管理</span>
          </template>
          <el-menu-item index="/project/list">项目列表</el-menu-item>
          <el-menu-item v-if="userRole === 'TEACHER'" index="/project/add">添加项目</el-menu-item>
        </el-sub-menu>
        
        <!-- 选题管理 -->
        <el-sub-menu index="selection">
          <template #title>
            <el-icon><Check /></el-icon>
            <span>选题管理</span>
          </template>
          <el-menu-item index="/selection/list">选题列表</el-menu-item>
          <el-menu-item v-if="userRole === 'STUDENT'" index="/selection/apply">申请选题</el-menu-item>
          <el-menu-item v-if="userRole === 'TEACHER'" index="/selection/approve">审核选题</el-menu-item>
        </el-sub-menu>
        
        <!-- 论文管理 -->
        <el-sub-menu index="thesis">
          <template #title>
            <el-icon><Link /></el-icon>
            <span>论文管理</span>
          </template>
          <el-menu-item index="/thesis/list">论文列表</el-menu-item>
          <el-menu-item v-if="userRole === 'STUDENT'" index="/thesis/upload">上传论文</el-menu-item>
          <el-menu-item v-if="userRole === 'TEACHER'" index="/thesis/review">评审论文</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="(item, index) in breadcrumbList" :key="index">
              {{ item.label }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar" />
              <span class="user-name">{{ userName }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleProfile">
                  <el-icon><User /></el-icon>
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item @click="handleSettings">
                  <el-icon><Setting /></el-icon>
                  <span>系统设置</span>
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 主内容区域 -->
      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { ElMessage } from 'element-plus'

// 导入图标
import { 
  House, User, Document, Check, Link, 
  Expand, Fold, ArrowDown, Setting, SwitchButton 
} from '@element-plus/icons-vue'

// 路由实例
const router = useRouter()
const route = useRoute()

// 用户状态管理
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapsed = ref(false)

// 侧边栏宽度
const asideWidth = computed(() => isCollapsed.value ? '60px' : '200px')

// 用户信息
const userInfo = computed(() => userStore.getUserInfo)
const userName = computed(() => userInfo.value?.name || userInfo.value?.username || '未知用户')
const userRole = computed(() => userInfo.value?.role || '')
const userAvatar = computed(() => {
  // 简单的头像生成逻辑，实际项目中可以根据用户信息生成不同的头像
  return `https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png`
})

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 面包屑列表
const breadcrumbList = ref([])

// 更新面包屑
const updateBreadcrumb = (path) => {
  const breadcrumbMap = {
    '/home': { label: '首页' },
    '/user/list': { label: '用户列表', parent: '用户管理' },
    '/user/add': { label: '添加用户', parent: '用户管理' },
    '/user/edit': { label: '编辑用户', parent: '用户管理' },
    '/project/list': { label: '项目列表', parent: '项目管理' },
    '/project/add': { label: '添加项目', parent: '项目管理' },
    '/project/edit': { label: '编辑项目', parent: '项目管理' },
    '/selection/list': { label: '选题列表', parent: '选题管理' },
    '/selection/apply': { label: '申请选题', parent: '选题管理' },
    '/selection/approve': { label: '审核选题', parent: '选题管理' },
    '/thesis/list': { label: '论文列表', parent: '论文管理' },
    '/thesis/upload': { label: '上传论文', parent: '论文管理' },
    '/thesis/review': { label: '评审论文', parent: '论文管理' }
  }
  
  const pathSegments = path.split('/').filter(segment => segment)
  const breadcrumbs = [{ label: '首页', path: '/home' }]
  
  let currentPath = ''
  pathSegments.forEach((segment, index) => {
    currentPath += `/${segment}`
    const breadcrumbInfo = breadcrumbMap[currentPath]
    if (breadcrumbInfo) {
      breadcrumbs.push({
        label: breadcrumbInfo.label,
        path: currentPath
      })
    }
  })
  
  breadcrumbList.value = breadcrumbs
}

// 监听路由变化，更新面包屑
watch(
  () => route.path,
  (newPath) => {
    updateBreadcrumb(newPath)
  },
  { immediate: true }
)

// 处理个人中心
const handleProfile = () => {
  ElMessage.info('个人中心功能开发中...')
}

// 处理系统设置
const handleSettings = () => {
  ElMessage.info('系统设置功能开发中...')
}

// 处理退出登录
const handleLogout = () => {
  // 清除用户信息
  userStore.logout()
  
  // 显示退出成功消息
  ElMessage.success('退出登录成功')
  
  // 跳转到登录页
  router.push('/login')
}

// 组件挂载时恢复用户信息
onMounted(() => {
  userStore.restoreUserInfo()
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.layout-aside {
  background-color: #304156;
  color: white;
  overflow-y: auto;
  transition: width 0.3s ease;
}

.aside-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #405065;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  margin: 0;
  transition: all 0.3s ease;
}

.collapse-btn {
  color: #fff;
}

.layout-menu {
  border-right: none;
  background-color: #304156;
}

.layout-header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left {
  flex: 1;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f0f0f0;
}

.user-name {
  margin: 0 8px;
}

.layout-main {
  background-color: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .layout-aside {
    width: 100%;
    position: fixed;
    top: 0;
    left: -100%;
    z-index: 1000;
    height: 100vh;
    transition: left 0.3s ease;
  }
  
  .layout-aside.is-collapsed {
    left: 0;
  }
  
  .layout-main {
    padding: 10px;
  }
}
</style>
