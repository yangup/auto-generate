import request from '@/utils/request'

class Api {

  systemUserSettingFind(query) {
    return this.get('/systemUserSetting/find', query)
  }

  /**
   * base function
   * **/
  // todo : base function
  get(url, params) {
    return request({
      url: url,
      method: 'get',
      params: params,
    })
  }

  getNoCheck(url, params, noCheck) {
    return request({
      url: url,
      noCheck: true,
      method: 'get',
      params: params,
    })
  }

  // todo : base function
  post(url, data, isFile) {
    return request({
      url: url,
      isFile: isFile,
      method: 'post',
      data,
    })
  }

  /**
   * @param {String} url
   * @param {Object} data
   * @param {Boolean} by : 当有错误的时候, 应用自己处理, 不统一处理
   * @returns {Object}
   */
  postAddUpdate(url, data, by) {
    return request({
      url: url,
      by: by,
      method: 'post',
      data,
    })
  }
}

export default new Api()
