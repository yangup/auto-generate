import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * bread: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }

 // 当设置 true 的时候该路由不会在侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 hidden: true // (默认 false)

 //当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 bread: 'noRedirect'
 详情见 : components/Breadcrumb/index.vue

 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 alwaysShow: true

 name: 'router-name' // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 meta: {
  roles: ['admin', 'editor'] // 设置该路由进入的权限，支持多个权限叠加
  title: 'title' // 设置该路由在侧边栏和面包屑中展示的名字
  icon: 'svg-name' // 设置该路由的图标
  breadcrumb: false //  如果设置为false，则不会在breadcrumb面包屑中显示(默认 true)
  affix: true // 若果设置为true，它则会固定在tags-view中(默认 false)

  // 当路由设置了该属性，则会高亮相对应的侧边栏。
  // 这在某些场景非常有用，比如：一个文章的列表页路由为：/article/list
  // 点击文章进入文章详情页，这时候路由为/article/1，但你想在侧边栏高亮文章列表的路由，就可以进行如下设置
  activeMenu: '/article/list'
}
 详情查看  : layout/Sidebar/SidebarItem.vue
 例如 : system/privilege?refresh=true
 refresh : true
 refresh : false
 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)

 例如 : /system/privilegeAdjust?refresh=true&fromActiveMenu=/system/privilege

 fromActiveMenu : 会在左边的菜单栏显示的是哪个的页面 ，默认是 path ，如果有 fromActiveMenu 就以 fromActiveMenu 为准

 所有的配置会以路由的配置 为一个默认配置,

 tagsView : false
 false : 不会在 tagsView 控件中显示

 */
export let leftRoutes = [
  /**
   * 系统管理
   * **/
  {
    // 用户管理
    path: '/system/user',
    component: () => import('@/views/system/user'),
  },
  // todo : auto-generate
  {
    path: '/system/systemUserSetting',
    component: () => import('@/views/system/systemUserSetting'),
  }

]

/**
 * 固定到左边,不参与权限管理
 */
export let leftFixedRoutes = [
  {
    path: '/dashboard',
    component: () => import('@/views/dashboard/index'),
    meta: { title: 'Dashboard', icon: 'dashboard', affix: true },
  },
]

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const noShowRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index'),
      },
    ],
  },
  {
    path: '/login',
    component: () => import('@/views/login/login'),
    hidden: true,
  },
  {
    path: '/updatePassword',
    component: () => import('@/views/login/updatePassword'),
    hidden: true,
  },
  {
    // todo : 默认转向 dashboard
    path: '/',
    redirect: '/dashboard',
  },
]

function getLeftRoutes() {
  let parentList = []
  let parentObj = {}
  let children = leftRoutes.concat(leftFixedRoutes)
  parentObj.path = '/allParentRouterParentObjPath'
  parentObj.component = Layout
  parentObj.children = children
  parentList.push(parentObj)
  return parentList
}

// todo : 注册路由
const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  // routes: noShowRoutes.concat(leftRoutes),
  routes: noShowRoutes.concat(getLeftRoutes()),
})

const router = createRouter()

export default router
