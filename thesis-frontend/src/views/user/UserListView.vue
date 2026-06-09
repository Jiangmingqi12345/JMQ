<template>
  <div class="user-list-container">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" size="small" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加用户
          </el-button>
        </div>
      </template>
      
      <el-row :gutter="20" style="margin-bottom: 20px">
        <el-col :xs="24" :sm="12" :md="8">
          <el-input
            placeholder="请输入用户名"
            v-model="searchForm.username"
            clearable
            style="margin-bottom: 10px"
          >
            <template #append>
              <el-button @click="handleSearch"><el-icon><Search /></el-icon></el-button>
            </template>
          </el-input>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-select
            placeholder="请选择角色"
            v-model="searchForm.role"
            clearable
            style="margin-bottom: 10px"
          >
            <el-option label="全部" value=""></el-option>
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="教师" value="TEACHER"></el-option>
            <el-option label="学生" value="STUDENT"></el-option>
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-col>
      </el-row>
      
      <el-table :data="userList" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" align="center"></el-table-column>
        <el-table-column prop="username" label="用户名" width="150"></el-table-column>
        <el-table-column prop="name" label="姓名" width="120"></el-table-column>
        <el-table-column prop="role" label="角色" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : scope.row.role === 'TEACHER' ? 'success' : 'primary'">
              {{ scope.row.role === 'ADMIN' ? '管理员' : scope.row.role === 'TEACHER' ? '教师' : '学生' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180"></el-table-column>
        <el-table-column prop="major" label="专业" width="120"></el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
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
import { ElMessage } from 'element-plus'
import service from '../../utils/axios'
import dayjs from 'dayjs'

// 导入图标
import { Plus, Search } from '@element-plus/icons-vue'

// 路由实例
const router = useRouter()

// 加载状态
const loading = ref(false)

// 搜索表单
const searchForm = reactive({
  username: '',
  role: ''
})

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

// 用户列表数据
const userList = ref([])

// 格式化时间
const formatDate = (date) => {
  if (!date) {
    return '无创建时间'
  }
  return dayjs(date).format('YYYY-MM-DD HH:mm:ss')
}

// 获取用户列表
const getUserList = async () => {
  try {
    loading.value = true
    const response = await service.get('/users/list', {
      params: {
        page: pagination.currentPage,
        size: pagination.pageSize,
        username: searchForm.username,
        role: searchForm.role
      }
    })
    
    userList.value = response.data.data || []
    pagination.total = response.data.total || 0
  } catch (error) {
    ElMessage.error('获取用户列表失败：' + error.message)
    console.error('获取用户列表失败：', error)
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取用户列表
onMounted(() => {
  getUserList()
})

// 处理添加用户
const handleAdd = () => {
  router.push('/user/add')
}

// 处理编辑用户
const handleEdit = (id) => {
  router.push(`/user/edit/${id}`)
}

// 处理删除用户
const handleDelete = (id) => {
  ElMessage.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await service.delete(`/users/${id}`)
      ElMessage.success('删除成功')
      // 重新获取用户列表
      getUserList()
    } catch (error) {
      ElMessage.error('删除失败：' + error.message)
      console.error('删除用户失败：', error)
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 处理搜索
const handleSearch = () => {
  // 重置当前页为1
  pagination.currentPage = 1
  // 获取用户列表
  getUserList()
}

// 处理重置
const handleReset = () => {
  searchForm.username = ''
  searchForm.role = ''
  // 重置当前页为1
  pagination.currentPage = 1
  // 获取用户列表
  getUserList()
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  // 获取用户列表
  getUserList()
}

// 处理当前页变化
const handleCurrentChange = (page) => {
  pagination.currentPage = page
  // 获取用户列表
  getUserList()
}
</script>

<style scoped>
.user-list-container {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
