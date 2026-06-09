<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <div class="not-found-code">404</div>
      <h2 class="not-found-title">页面未找到</h2>
      <p class="not-found-message">抱歉，您访问的页面不存在或已被移除</p>
      <el-button type="primary" @click="goBack" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()

const goBack = () => {
  router.push('/')
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.not-found-content {
  text-align: center;
  padding: 40px;
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.not-found-code {
  font-size: 120px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 20px;
  line-height: 1;
}

.not-found-title {
  font-size: 32px;
  color: #303133;
  margin-bottom: 16px;
}

.not-found-message {
  font-size: 16px;
  color: #606266;
  margin-bottom: 32px;
}

.back-btn {
  font-size: 16px;
  padding: 12px 32px;
}
</style>
