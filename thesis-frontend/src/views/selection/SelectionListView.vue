<template>
  <div class="selection-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>选题列表</span>
        </div>
      </template>
      
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            placeholder="请输入项目名称"
            v-model="searchForm.name"
            clearable
            style="margin-bottom: 10px"
          >
            <template #append>
              <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
      
      <el-table :data="selectionList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="studentId" label="学生ID" width="120"></el-table-column>
        <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
        <el-table-column prop="projectName" label="项目名称" min-width="200"></el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusColor(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="selectTime" label="选择时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.selectTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button v-if="scope.row.status === 'PENDING'" type="success" size="small" @click="handleApprove(scope.row.id)">审核</el-button>
            <el-button type="info" size="small" @click="handleView(scope.row.id)">查看</el-button>
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
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import service from '../../utils/axios'

// 导入图标
import { Search } from '@element-plus/icons-vue'

// 路由实例
const router = useRouter()

// 搜索表单
const searchForm = reactive({
  name: ''
})

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

// 获取选题列表
const getSelectionList = async () => {
  try {
    loading.value = true
    const response = await service.get('/selections/list', {
      params: {
        page: pagination.currentPage,
        size: pagination.pageSize,
        name: searchForm.name
      }
    })
    
    // 更新选题列表数据
    if (response && response.data) {
      selectionList.value = response.data.data || []
      pagination.total = response.data.total || 0
    } else {
      selectionList.value = []
      pagination.total = 0
    }
  } catch (error) {
    ElMessage.error('获取选题列表失败：' + (error.message || '未知错误'))
    console.error('获取选题列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  // 重置当前页为1
  pagination.currentPage = 1
  // 重新获取选题列表
  getSelectionList()
}

// 处理重置
const handleReset = () => {
  searchForm.name = ''
  // 重置当前页为1
  pagination.currentPage = 1
  // 重新获取选题列表
  getSelectionList()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  // 重新获取选题列表
  getSelectionList()
}

// 处理当前页变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  // 重新获取选题列表
  getSelectionList()
}

// 处理审核
const handleApprove = (id) => {
  router.push(`/selection/approve?id=${id}`)
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

// 处理查看
const handleView = (id) => {
  // 跳转到查看页面，传递选题ID
  router.push(`/selection/view?id=${id}`)
}

// 组件挂载时获取选题列表
onMounted(() => {
  getSelectionList()
})
</script>

<style scoped>
.selection-list-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>