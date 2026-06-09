<template>
  <div class="home-container">
    <el-card class="welcome-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>欢迎使用毕业设计管理系统</span>
        </div>
      </template>
      <div class="card-content">
        <div class="welcome-text">
          <h2>您好，{{ userName }}！</h2>
          <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
        </div>
        
        <div class="role-info">
          <el-tag :type="roleTagType" size="large">{{ roleText }}</el-tag>
        </div>
      </div>
    </el-card>
    
    <div class="stats-container">
      <el-row :gutter="20">
        <!-- 项目总数 -->
        <el-col :xs="24" :sm="12" :lg="6">
          <el-card class="stat-card project-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-info">
                <div class="stat-value">{{ projectCount }}</div>
                <div class="stat-label">项目总数</div>
              </div>
              <div class="stat-icon">
                <el-icon class="icon-large"><Document /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <!-- 选题总数 -->
        <el-col :xs="24" :sm="12" :lg="6">
          <el-card class="stat-card selection-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-info">
                <div class="stat-value">{{ selectionCount }}</div>
                <div class="stat-label">选题总数</div>
              </div>
              <div class="stat-icon">
                <el-icon class="icon-large"><Check /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <!-- 论文总数 -->
        <el-col :xs="24" :sm="12" :lg="6">
          <el-card class="stat-card thesis-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-info">
                <div class="stat-value">{{ thesisCount }}</div>
                <div class="stat-label">论文总数</div>
              </div>
              <div class="stat-icon">
                <el-icon class="icon-large"><Link /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
        
        <!-- 用户总数 -->
        <el-col :xs="24" :sm="12" :lg="6">
          <el-card class="stat-card user-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-info">
                <div class="stat-value">{{ userCount }}</div>
                <div class="stat-label">用户总数</div>
              </div>
              <div class="stat-icon">
                <el-icon class="icon-large"><User /></el-icon>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    
    <div class="recent-activity">
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>最近活动</span>
            <el-button type="text" size="small">查看更多</el-button>
          </div>
        </template>
        <el-timeline>
          <el-timeline-item
            v-for="(item, index) in recentActivities"
            :key="index"
            :timestamp="item.time"
            :icon="item.icon"
            :type="item.type"
          >
            {{ item.content }}
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import dayjs from 'dayjs'

// 导入图标
import { Document, Check, Link, User } from '@element-plus/icons-vue'

// 用户状态管理
const userStore = useUserStore()

// 用户名称
const userName = computed(() => userStore.getUserInfo?.name || userStore.getUserInfo?.username || '未知用户')

// 用户角色
const userRole = computed(() => userStore.getUserInfo?.role || '')

// 角色文本
const roleText = computed(() => {
  const roleMap = {
    'ADMIN': '系统管理员',
    'TEACHER': '指导教师',
    'STUDENT': '学生'
  }
  return roleMap[userRole.value] || '未知角色'
})

// 角色标签类型
const roleTagType = computed(() => {
  const typeMap = {
    'ADMIN': 'danger',
    'TEACHER': 'success',
    'STUDENT': 'primary'
  }
  return typeMap[userRole.value] || 'info'
})

// 当前日期
const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日 HH:mm')
})

// 统计数据（模拟）
const projectCount = ref(128)
const selectionCount = ref(95)
const thesisCount = ref(78)
const userCount = ref(256)

// 最近活动（模拟）
const recentActivities = ref([
  { 
    time: '2023-12-02 14:30', 
    content: '您成功登录系统', 
    icon: 'User', 
    type: 'success'
  },
  { 
    time: '2023-12-02 14:00', 
    content: '学生张三上传了论文', 
    icon: 'Document', 
    type: 'info'
  },
  { 
    time: '2023-12-02 13:45', 
    content: '教师李四审核通过了选题申请', 
    icon: 'Check', 
    type: 'success'
  },
  { 
    time: '2023-12-02 13:30', 
    content: '系统更新了公告', 
    icon: 'Bell', 
    type: 'warning'
  },
  { 
    time: '2023-12-02 13:15', 
    content: '学生王五提交了选题申请', 
    icon: 'Edit', 
    type: 'primary'
  }
])

// 组件挂载时获取数据
onMounted(() => {
  // 这里可以调用API获取真实数据
  // fetchStats()
  // fetchRecentActivities()
})

// 获取统计数据（模拟）
const fetchStats = () => {
  // 模拟API请求
  setTimeout(function() {
    projectCount.value = 128
    selectionCount.value = 95
    thesisCount.value = 78
    userCount.value = 256
  }, 500)
}

// 获取最近活动（模拟）
const fetchRecentActivities = () => {
  // 模拟API请求
  setTimeout(function() {
    recentActivities.value = [
      { 
        time: dayjs().format('YYYY-MM-DD HH:mm'), 
        content: '您成功登录系统', 
        icon: 'User', 
        type: 'success'
      },
      { 
        time: dayjs().subtract(30, 'minute').format('YYYY-MM-DD HH:mm'), 
        content: '学生张三上传了论文', 
        icon: 'Document', 
        type: 'info'
      },
      { 
        time: dayjs().subtract(45, 'minute').format('YYYY-MM-DD HH:mm'), 
        content: '教师李四审核通过了选题申请', 
        icon: 'Check', 
        type: 'success'
      },
      { 
        time: dayjs().subtract(60, 'minute').format('YYYY-MM-DD HH:mm'), 
        content: '系统更新了公告', 
        icon: 'Bell', 
        type: 'warning'
      },
      { 
        time: dayjs().subtract(75, 'minute').format('YYYY-MM-DD HH:mm'), 
        content: '学生王五提交了选题申请', 
        icon: 'Edit', 
        type: 'primary'
      }
    ]
  }, 500)
}
</script>

<style scoped>
.home-container {
  width: 100%;
}

.welcome-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.welcome-text {
  flex: 1;
}

.welcome-text h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.welcome-text p {
  margin: 0;
  color: #606266;
  font-size: 16px;
}

.role-info {
  display: flex;
  align-items: center;
}

.stats-container {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
  border-left: 4px solid #409eff;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.15) !important;
}

.project-card {
  border-left-color: #409eff;
}

.selection-card {
  border-left-color: #67c23a;
}

.thesis-card {
  border-left-color: #e6a23c;
}

.user-card {
  border-left-color: #f56c6c;
}

.stat-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.selection-card .stat-icon {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.thesis-card .stat-icon {
  background-color: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.user-card .stat-icon {
  background-color: rgba(245, 108, 108, 0.1);
  color: #f56c6c;
}

.icon-large {
  font-size: 32px;
}

.recent-activity {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .welcome-text h2 {
    font-size: 20px;
  }
  
  .stat-value {
    font-size: 24px;
  }
}
</style>
