<template>
  <div class="project-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>项目列表</span>
          <el-button type="primary" size="small" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加项目
          </el-button>
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
      
      <el-table :data="projectList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="title" label="项目名称" min-width="200"></el-table-column>
        <el-table-column prop="description" label="项目描述" min-width="300"></el-table-column>
        <el-table-column prop="maxStudents" label="人数限制" width="120" align="center"></el-table-column>
        <el-table-column prop="selectedCount" label="已选人数" width="120" align="center"></el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
              {{ scope.row.status === 0 ? '进行中' : '已结束' }}
            </el-tag>
            <el-tag v-if="scope.row.maxStudents && scope.row.selectedCount >= scope.row.maxStudents" type="warning" style="margin-left: 8px">人满</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row.id)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import service from '../../utils/axios'

// 导入图标
import { Plus, Search } from '@element-plus/icons-vue'

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

// 项目列表数据
const projectList = ref([])

// 加载状态
const loading = ref(false)

// 获取项目列表
const getProjectList = async () => {
  try {
    loading.value = true
    const response = await service.get('/projects/all', {
      params: {
        page: pagination.currentPage,
        size: pagination.pageSize,
        name: searchForm.name
      }
    })
    
    // 更新项目列表数据
    projectList.value = response.data.data || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取项目列表失败：' + (error.message || '未知错误'))
    console.error('获取项目列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 处理添加项目
const handleAdd = () => {
  router.push('/project/add')
}

// 处理编辑项目
const handleEdit = (id) => {
  router.push(`/project/edit/${id}`)
}

// 处理删除项目
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该项目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用API删除项目
    await service.delete(`/projects/${id}`)
    
    // 显示成功消息
    ElMessage.success('删除成功')
    
    // 重新获取项目列表
    getProjectList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '未知错误'))
      console.error('删除项目失败：', error)
    }
  }
}

// 处理搜索
const handleSearch = () => {
  // 重置当前页为1
  pagination.currentPage = 1
  // 重新获取项目列表
  getProjectList()
}

// 处理重置
const handleReset = () => {
  searchForm.name = ''
  // 重置当前页为1
  pagination.currentPage = 1
  // 重新获取项目列表
  getProjectList()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  // 重新获取项目列表
  getProjectList()
}

// 处理当前页变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  // 重新获取项目列表
  getProjectList()
}

// 组件挂载时获取项目列表
onMounted(() => {
  getProjectList()
})
</script>

<style scoped>
.project-list-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>