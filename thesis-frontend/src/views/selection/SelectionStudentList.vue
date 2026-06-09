<template>
  <div class="selection-student-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的选题列表</span>
        </div>
      </template>
      
      <el-table :data="selectionList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="projectName" label="项目名称" min-width="200"></el-table-column>
        <el-table-column prop="teacherName" label="指导老师" width="150"></el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusColor(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="selectTime" label="申请时间" width="180"></el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.status === 'PENDING'" type="danger" size="small" @click="handleCancel(scope.row.id)">取消申请</el-button>
            <el-button type="info" size="small" @click="handleView(scope.row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div style="margin-top: 20px">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        ></el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import service from '../../utils/axios'

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 选题列表数据
const selectionList = ref([])

// 加载状态
const loading = ref(false)

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

// 获取学生选题列表
const getSelectionList = async () => {
  try {
    loading.value = true
    const response = await service.get('/selections/student', {
      params: {
        page: pagination.currentPage,
        size: pagination.pageSize
      }
    })
    
    // 更新选题列表数据
    selectionList.value = response.data.data || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取选题列表失败：' + (error.message || '未知错误'))
    console.error('获取选题列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  getSelectionList()
}

// 处理当前页变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  getSelectionList()
}

// 处理取消申请
const handleCancel = async (id) => {
  try {
    await service.delete(`/selections/${id}/cancel`)
    ElMessage.success('取消申请成功')
    // 重新获取列表
    getSelectionList()
  } catch (error) {
    ElMessage.error('取消申请失败：' + (error.message || '未知错误'))
    console.error('取消申请失败：', error)
  }
}

// 处理查看详情
const handleView = (id) => {
  // 跳转到查看详情页面
  // router.push(`/selection/detail/${id}`)
  ElMessage.info('查看详情功能开发中...')
}

// 组件挂载时获取选题列表
onMounted(() => {
  getSelectionList()
})
</script>

<style scoped>
.selection-student-list-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>