import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/login/LoginView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { title: '登录' }
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/login/RegisterView.vue'),
      meta: { title: '注册' }
    },
    {
      path: '/',
      name: 'layout',
      component: () => import('../views/layout/LayoutView.vue'),
      redirect: '/home',
      children: [
        {
          path: '/home',
          name: 'home',
          component: () => import('../views/HomeView.vue'),
          meta: { title: '首页', requiresAuth: true }
        },
        // 用户管理
        {
          path: '/user/list',
          name: 'userList',
          component: () => import('../views/user/UserListView.vue'),
          meta: { title: '用户列表', requiresAuth: true, role: 'ADMIN' }
        },
        {
          path: '/user/add',
          name: 'userAdd',
          component: () => import('../views/user/UserAddView.vue'),
          meta: { title: '添加用户', requiresAuth: true, role: 'ADMIN' }
        },
        {
          path: '/user/edit/:id',
          name: 'userEdit',
          component: () => import('../views/user/UserEditView.vue'),
          meta: { title: '编辑用户', requiresAuth: true, role: 'ADMIN' }
        },
        // 项目管理
        {
          path: '/project/list',
          name: 'projectList',
          component: () => import('../views/project/ProjectListView.vue'),
          meta: { title: '项目列表', requiresAuth: true }
        },
        {
          path: '/project/add',
          name: 'projectAdd',
          component: () => import('../views/project/ProjectAddView.vue'),
          meta: { title: '添加项目', requiresAuth: true, role: 'TEACHER' }
        },
        {
          path: '/project/edit/:id',
          name: 'projectEdit',
          component: () => import('../views/project/ProjectEditView.vue'),
          meta: { title: '编辑项目', requiresAuth: true, role: 'TEACHER' }
        },
        // 选题管理
        {
          path: '/selection/list',
          name: 'selectionList',
          component: () => import('../views/selection/SelectionListView.vue'),
          meta: { title: '选题列表', requiresAuth: true }
        },
        {
          path: '/selection/apply',
          name: 'selectionApply',
          component: () => import('../views/selection/SelectionApplyView.vue'),
          meta: { title: '申请选题', requiresAuth: true, role: 'STUDENT' }
        },
        {
          path: '/selection/approve',
          name: 'selectionApprove',
          component: () => import('../views/selection/SelectionApproveView.vue'),
          meta: { title: '审核选题', requiresAuth: true, role: 'TEACHER' }
        },
        {
          path: '/selection/view',
          name: 'selectionView',
          component: () => import('../views/selection/SelectionView.vue'),
          meta: { title: '查看选题', requiresAuth: true }
        },
        // 论文管理
        {
          path: '/thesis/list',
          name: 'thesisList',
          component: () => import('../views/thesis/ThesisListView.vue'),
          meta: { title: '论文列表', requiresAuth: true }
        },
        {
          path: '/thesis/detail',
          name: 'thesisDetail',
          component: () => import('../views/thesis/ThesisDetailView.vue'),
          meta: { title: '论文详情', requiresAuth: true }
        },
        {
          path: '/thesis/upload',
          name: 'thesisUpload',
          component: () => import('../views/thesis/ThesisUploadView.vue'),
          meta: { title: '上传论文', requiresAuth: true, role: 'STUDENT' }
        },
        {
          path: '/thesis/review',
          name: 'thesisReview',
          component: () => import('../views/thesis/ThesisReviewView.vue'),
          meta: { title: '评审论文', requiresAuth: true, role: 'TEACHER' }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('../views/NotFoundView.vue'),
      meta: { title: '404' }
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = `${to.meta.title} - 毕业设计管理系统`
  
  // 获取本地存储的用户信息
  const user = localStorage.getItem('user')
  const token = localStorage.getItem('token')
  
  // 判断路由是否需要登录
  if (to.meta.requiresAuth) {
    if (!user || !token) {
      // 未登录，跳转到登录页
      next({ name: 'login' })
    } else {
      // 已登录，检查角色权限
        const userInfo = JSON.parse(user)
        // 管理员可以访问所有页面，教师和学生只能访问对应角色的页面
        if (to.meta.role && to.meta.role !== userInfo.role && userInfo.role !== 'ADMIN') {
          // 角色不匹配且不是管理员，跳转到首页
          next({ name: 'home' })
        } else {
          // 角色匹配或管理员，允许访问
          next()
        }
    }
  } else {
    // 不需要登录，直接访问
    next()
  }
})

// 添加一个全局方法，用于检查用户是否已登录（包括Session有效性）
router.checkLogin = async () => {
  const user = localStorage.getItem('user')
  const token = localStorage.getItem('token')
  
  if (!user || !token) {
    return false
  }
  
  try {
    // 这里可以添加一个API调用来检查Session有效性
    // 例如：await service.get('/check-login')
    return true
  } catch (error) {
    return false
  }
}

export default router
