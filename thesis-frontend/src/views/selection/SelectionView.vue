<template>
  <div class="selection-view-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>选题详情</span>
          <el-button type="primary" @click="handleBack" style="margin-left: 10px">返回列表</el-button>
        </div>
      </template>
      
      <div class="selection-detail" v-if="selectionDetail">
        <el-form label-width="120px" size="large">
          <el-row>
            <el-col :span="12">
              <el-form-item label="学生ID">
                <el-input v-model="selectionDetail.studentId" disabled></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学生姓名">
                <el-input v-model="selectionDetail.studentName" disabled></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="项目名称">
                <el-input v-model="selectionDetail.projectName" disabled></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="选择时间">
                <el-input :value="formatDate(selectionDetail.selectTime)" disabled></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态">
                <el-tag :type="getStatusColor(selectionDetail.status)">
                  {{ getStatusText(selectionDetail.status) }}
                </el-tag>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      
      <!-- 加载状态 -->
      <div v-else class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import service from '../../utils/axios'

// 路由实例
const router = useRouter()
const route = useRoute()

// 选题详情
const selectionDetail = ref(null)

// 获取状态颜色
const getStatusColor = (status) => {
  switch (status) {
    case 'PENDING':
      return 'warning'
    case 'APPROVED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    default:
      return 'info'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'PENDING':
      return '待审核'
    case 'APPROVED':
      return '已通过'
    case 'REJECTED':
      return '已拒绝'
    default:
      return '未知状态'
  }
}

// 格式化日期为YYYY-MM-DD
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 获取选题详情
const getSelectionDetail = async () => {
  try {
    // 从URL参数中获取选题ID
    const selectionId = route.query.id
    if (!selectionId) {
      ElMessage.error('未找到选题ID')
      handleBack()
      return
    }
    
    // 发送请求获取选题详情
    const response = await service.get(`/selections/${selectionId}`)
    
    // 更新选题详情数据
    if (response && response.data) {
      // 统一处理响应数据，兼容不同的响应格式
      selectionDetail.value = response.data.data || response.data || null
      
      if (!selectionDetail.value) {
        ElMessage.error('未找到选题信息')
      }
    } else {
      ElMessage.error('获取选题详情失败：无效的响应格式')
      selectionDetail.value = null
    }
  } catch (error) {
    // 处理错误，显示友好的错误信息
    let errorMsg = '获取选题详情失败'
    if (error.response && error.response.status === 400) {
      errorMsg = '无效的选题请求'
    } else if (error.message) {
      errorMsg = `获取选题详情失败：${error.message}`
    }
    
    ElMessage.error(errorMsg)
    console.error('获取选题详情失败：', error)
    // 发生错误时返回列表页
    handleBack()
  }
}

// 处理返回
const handleBack = () => {
  router.push('/selection/list')
}

// 组件挂载时获取选题详情
onMounted(() => {
  getSelectionDetail()
})
</script>

<style scoped>
.selection-view-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selection-detail {
  margin-top: 20px;
}

.loading-container {
  margin-top: 20px;
}
</style>