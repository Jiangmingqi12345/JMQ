<template>
  <div class="thesis-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>论文列表</span>
        </div>
      </template>
      
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
            placeholder="请输入论文标题"
            v-model="searchForm.title"
            clearable
            style="margin-bottom: 10px"
          >
            <template #append>
              <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-input
            placeholder="请输入学生姓名"
            v-model="searchForm.studentName"
            clearable
            style="margin-bottom: 10px"
          >
            <template #append>
              <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-select
            placeholder="请选择论文状态"
            v-model="searchForm.status"
            clearable
            style="margin-bottom: 10px"
          >
            <el-option label="已上传" value="UPLOADED" />
            <el-option label="已通过" value="PASSED" />
            <el-option label="已驳回" value="REJECTED" />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
      
      <el-table :data="thesisList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column label="论文标题" min-width="250">
          <template #default="scope">
            {{ scope.row.title || '无标题' }}
          </template>
        </el-table-column>
        <el-table-column prop="studentName" label="学生姓名" width="150"></el-table-column>
        <el-table-column label="项目名称" width="200">
          <template #default="scope">
            {{ scope.row.projectName || '无项目' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="getStatusColor(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="上传时间" width="180" align="center">
          <template #default="scope">
            {{ formatUploadTime(scope.row.uploadTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="350" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleView(scope.row.id)">查看</el-button>
            <el-button v-if="scope.row.status === 'UPLOADED'" type="success" size="small" @click="handleReview(scope.row.id)">评审</el-button>
            <el-button v-if="scope.row.status === 'PASSED' || scope.row.status === 'REJECTED'" type="info" size="small" @click="handleReview(scope.row.id)">重新评审</el-button>
            <el-button type="warning" size="small" @click="handleDownload(scope.row.id)">下载</el-button>
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
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import service from '../../utils/axios'
import dayjs from 'dayjs'

// 导入图标
import { Search } from '@element-plus/icons-vue'

// 路由实例
const router = useRouter()
const route = useRoute()

// 搜索表单
const searchForm = reactive({
  title: '',
  studentName: '',
  status: ''
})

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 加载状态
const loading = ref(false)

// 论文列表数据
const thesisList = ref([])

// 获取状态颜色
const getStatusColor = (status) => {
  switch (status) {
    case 'uploaded':
    case 'UPLOADED':
      return 'info'
    case 'reviewed':
    case 'REVIEWED':
    case 'PASSED':
    case 'passed':
      return 'success'
    case 'rejected':
    case 'REJECTED':
      return 'danger'
    default:
      return 'warning'
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'uploaded':
    case 'UPLOADED':
      return '已上传'
    case 'reviewed':
    case 'REVIEWED':
    case 'PASSED':
    case 'passed':
      return '已通过'
    case 'rejected':
    case 'REJECTED':
      return '已驳回'
    default:
      return '未知状态'
  }
}

// 格式化上传时间
const formatUploadTime = (uploadTime) => {
  if (!uploadTime) {
    return '无上传时间'
  }
  return dayjs(uploadTime).format('YYYY-MM-DD HH:mm:ss')
}

// 获取论文列表
const getThesisList = async () => {
  try {
      loading.value = true
      const response = await service.get('/thesis/list', {
        params: {
          page: pagination.currentPage,
          size: pagination.pageSize,
          title: searchForm.title,
          studentName: searchForm.studentName,
          status: searchForm.status
        }
      })
      
      // 添加调试信息
      console.log('完整响应数据:', response)
      console.log('响应数据结构:', JSON.stringify(response, null, 2))
      
      // 更新论文列表数据
      thesisList.value = response.data.data || []
      pagination.total = response.data.total || 0
      
      // 打印处理后的数据
      console.log('处理后的数据:', thesisList.value)
    } catch (error) {
      ElMessage.error('获取论文列表失败：' + (error.message || '未知错误'))
      console.error('获取论文列表失败：', error)
      console.error('错误详情:', JSON.stringify(error, null, 2))
    } finally {
      loading.value = false
    }
}

// 处理搜索
const handleSearch = () => {
  // 重置当前页为1
  pagination.currentPage = 1
  // 调用API获取数据
  getThesisList()
}

// 处理重置
const handleReset = () => {
  searchForm.title = ''
  // 重置当前页为1
  pagination.currentPage = 1
  // 调用API获取数据
  getThesisList()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  // 调用API获取数据
  getThesisList()
}

// 处理当前页变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  // 调用API获取数据
  getThesisList()
}

// 处理查看
const handleView = (id) => {
  router.push(`/thesis/detail?id=${id}`)
}

// 处理评审
const handleReview = (id) => {
  router.push(`/thesis/review?id=${id}`)
}

// 处理下载
const handleDownload = (id) => {
  // 直接通过window.open打开下载链接
  window.open(`http://localhost:8080/thesis/download/${id}`, '_blank')
}

// 组件挂载时获取论文列表
onMounted(() => {
  getThesisList()
})

// 路由更新时重新获取数据，确保看到最新状态
watch(() => route.query, () => {
  getThesisList()
}, { deep: true })
</script>

<style scoped>
.thesis-list-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>