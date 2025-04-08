import md5 from 'js-md5'

// 长度不一样, 值肯定不一样
export const USER_AGENT_MD5_RAW = md5(navigator.userAgent).toUpperCase()
// 64 位
export const USER_AGENT_MD5_UPPER = USER_AGENT_MD5_RAW + md5(USER_AGENT_MD5_RAW).toUpperCase()
export const USER_AGENT_MD5_LOW = USER_AGENT_MD5_UPPER.toLowerCase()
export const TOKEN_KEY = USER_AGENT_MD5_UPPER.substring(1, 2) // 1
export const CACHED_VIEWS = USER_AGENT_MD5_LOW.substring(0, 5) // 5
export const VISITED_VIEWS = USER_AGENT_MD5_UPPER.substring(3, 9) // 6
export const PRIVILEGE_USER = USER_AGENT_MD5_LOW.substring(6, 13) // 7
export const USER = USER_AGENT_MD5_UPPER.substring(10, 18) // 8
export const USER_SETTING = USER_AGENT_MD5_LOW.substring(12, 22) // 10
export const SHOW_MORE_STATUS = USER_AGENT_MD5_LOW.substring(14, 40) // 26
export const SIDEBAR_STATUS = USER_AGENT_MD5_UPPER.substring(15, 26) // 11
export const CANCEL_ALERT = USER_AGENT_MD5_LOW.substring(15, 27) // 12
export const F_TOKEN = USER_AGENT_MD5_UPPER.substring(10, 19) // 9

/**
 * @param {Object} obj
 * @param {int} index
 * @returns {Object}
 * object key
 */
export function OBJ_K(obj, index) {
  if (obj) {
    return Object.keys(obj)[index]
  }
  return ''
}

/**
 * @param {Object} obj
 * @param {int} index
 * @returns {Object}
 * object value
 */
export function OBJ_V(obj, index) {
  if (obj) {
    return Object.values(obj)[index]
  }
  return ''
}

/**
 * @param {Object} obj
 * @param {String} value
 * @returns {String}
 *  根据 value 获得 key
 */
export function OBJ_VALUE(obj, value) {
  let compare = (a, b) => a === b
  return Object.keys(obj).find(k => compare(obj[k], value))
}

// todo : auto-generate

export const PRIVILEGE_TYPE = {
  PARENT: '父菜单',
  CHILD: '子权限',
}

export const PRIVILEGE_STATUS = {
  ON: '启用',
  OFF: '禁用',
}

export const USER_STATUS = {
  ON: '启用',
  OFF: '禁用',
}

export const MESSAGE_TYPE = {
  SYSTEM: '系统消息',
  NOTICE: '通知',
}

export const PRIVILEGE_TYPE_CHILD = OBJ_K(PRIVILEGE_TYPE, 1)
export const PRIVILEGE_TYPE_PARENT = OBJ_K(PRIVILEGE_TYPE, 0)

export const ROLE_STATUS = {
  ON: '启用',
  OFF: '禁用',
}

export const SEX = {
  MALE: '男',
  FEMALE: '女',
}

export const YES_NO = {
  YES: '是',
  NO: '否',
}

export const TASK_TYPE = {
  TASK: '任务',
  ACTIVITY: '活动',
}

export const privilegeServerType = {
  REGULAR_EXPRESSION: '正则表达式',
  FUZZY_MATCHING: '带*模糊匹配',
  PERFECT_MATCH: '完全匹配',
}

// todo : auto-generate
export const APP_STATUS = {
  ON: '启用',
  OFF: '禁用',
}
export const DEMO100_STATUS = {
  CREATED: '新产生',
  DONE: '完成',
  RETURN: '打回',
  REJECT: '拒绝',
  CLOSED: '关闭',
}
export const TYPE_TYPE = {
  NONE: '无',
  ACTIVITY: '活动',
  LINE: '线路',
  SHARE: '分享',
  AREA_PLACE: '区域',
}
export const TYPE_STATUS = {
  NONE: '无',
  SUGGEST: '推荐',
  AREA: '区域',
  HOT: '热点',
}
export const USER_TYPE = {
  ADMIN: '系统用户',
  OTHER: '其他类型的用户',
}
export const DEMO5_STATUS = {
  ON: '开启',
  OFF: '关闭',
}
export const DEMO5_TYPE = {
  CREATED: '新产生',
  DONE: '完成',
}
export const DEMO5_RESPONSE_STATUS = {
  CREATED: '新产生',
  DONE: '完成',
  CLOSED: '关闭',
}
export const DEMO5_RESPONSE_TYPE = {
  CREATED: '新产生',
  DONE: '完成',
  CLOSED: '关闭',
  FAILED: '失败',
}
export const DEMO5_REQUEST_STATUS = {
  CREATED: '新产生',
  DONE: '完成',
  CLOSED: '关闭',
  FAILED: '失败',
  FAILED1: '失败1',
}
export const DEMO5_REQUEST_TYPE = {
  CREATED: '新产生',
  DONE: '完成',
  CLOSED: '关闭',
  FAILED: '失败',
  FAILED1: '失败1',
  FAILED2: '失败2',
}
export const DEMO20_RESPONSE_STATUS = {
  CREATED: '新产生',
  DONE: '完成',
  CLOSED: '关闭',
  FAILED: '失败',
}

export const DEMO4_TYPE = {
  SYSTEM: '系统类型',
  CUSTOMER: '用户类型',
  OTHER: '其他类型',
}

export const DEMO4_STATUS = {
  CREATED: '新产生',
  DONE: '完成',
  RETURN: '打回',
  REJECT: '拒绝',
  CLOSED: '关闭',
}
