# “我的 / My Profile” 接入方案

## 现状梳理
- 前端登录态：`src/stores/user.js` 使用 Pinia 存储 token，并同步到 `localStorage` 的 `auth_token`，提供 `setToken`/`logout` 等方法。`isAuthenticated` 依赖 token 是否存在。
- 请求携带凭证：`src/api/axios.js` 统一创建 axios 实例，`baseURL` 为 `http://localhost:8080`；请求拦截器读取 Pinia user store，在有 token 时设置 `Authorization: Bearer <token>`，响应 401 时会注销并跳转登录。
- 路由守卫：`src/router/index.js` 声明了 `/`、`/problems`、`/community`、`/messages` 需要登录；`/login`、`/register` 仅限未登录。`beforeEach` 根据 `requiresAuth`/`guestOnly` 判断后跳转。
- 后端能力：
  - 认证：`POST /auth/register`（公开）、`POST /auth/login`（公开）返回 `token`、`userId`、`username`、`role`、`bindingStatus`。
  - 社区：`/community/posts` 支持创建、列表、详情、点赞、评论、删除（仅管理员），默认要求 JWT。
  - 消息中心：`/messages` 获取列表、`/messages/unread-count` 未读数、`PUT /messages/{id}/read` 标记已读，均需 JWT。
  - 用户绑定：`POST /user/bind` 提交绑定请求，需 JWT。
- 缺口：尚无 `GET /users/me` 之类的用户信息读取接口，也无按用户查询帖子/绑定历史等，无法在 My Profile 页刷新最新用户资料或个人帖子列表。

## 建议接口契约（新增/复用）
- **GET /users/me**（需 JWT）：返回 `{ id, username, role, bindingStatus, bindingIdentifier?, createdAt? }`，用于页面初始化/刷新。
- **PATCH /users/me**（需 JWT，可选）：允许更新个人信息（若有昵称/头像字段可扩展）。暂不实现逻辑也可，作为占位。
- **GET /community/posts?author=current**（需 JWT，可选新查询参数）：复用列表接口，增加 `author=current` 过滤当前用户帖子；也可新增 `/community/my-posts` 更清晰。
- **GET /user/bindings/history**（需 JWT，可选）：若需展示绑定审核记录，可在 `BindingService` 提供查询；当前仅有提交入口需补充。

## 路由与页面落点
- 新增视图文件：`frontend/src/views/MyProfileView.vue`，放在现有 `views` 目录。
- 路由配置：在 `src/router/index.js` 新增 `{ path: '/profile', name: 'profile', component: MyProfileView, meta: { requiresAuth: true } }`，沿用全局守卫即可，无需额外 guest 逻辑。
- 入口导航：可在 `App.vue` 的登录态操作区添加“我的”按钮跳转 `/profile`（与消息、退出并列）。

## 改动清单（按文件）
1. `frontend/src/views/MyProfileView.vue`（新增）：占位页面，读取 user store 并为后续 API 调用留出区块（例如用户信息卡、我的帖子列表）。
2. `frontend/src/router/index.js`（修改）：注册 `/profile` 路由，meta 标记 `requiresAuth: true`。
3. `frontend/src/App.vue`（小改）：在顶部导航增加“我的”入口，指向 `/profile`，仅在登录态展示。
4. `backend/src/main/java/com/example/onlinelearning/controller/UserController.java`（新增）：提供 `GET /users/me`（以及可选 `PATCH /users/me`）。
5. `backend/src/main/java/com/example/onlinelearning/service/UserService.java`（新增或扩展现有 UserRepository 调用）：根据 `Authentication.getName()` 查询用户并映射为 `UserResponse`（可扩展字段）。
6. `backend/src/main/java/com/example/onlinelearning/dto/UserResponse.java`（修改）：若需要前端展示更多信息，可补充 `bindingIdentifier`、`createdAt` 等字段。
7. `backend/src/main/java/com/example/onlinelearning/controller/CommunityController.java` & `CommunityService`（修改）：支持作者过滤参数或新增 `/community/my-posts` 路由，返回当前用户帖子列表。
8. （可选）`backend/src/main/java/com/example/onlinelearning/service/BindingService.java` 和相关 DTO/Controller：如果需要绑定历史展示，补充查询接口 `GET /user/bindings/history`。

## 接入策略
- 前端数据源：初次进入 `/profile` 时调用 `GET /users/me` 更新 Pinia user 数据；帖子列表区域调用新的“我的帖子”接口；绑定信息区域显示 `bindingStatus` 并提供“发起绑定”按钮复用现有 `POST /user/bind`。
- 鉴权：继续依赖 axios 拦截器自动带 JWT；路由 `requiresAuth` 保证未登录跳转登录页；后端接口由 Spring Security 默认保护。
- 逐步落地：本轮仅添加占位视图与路由、导航入口和接口草案，后续再填充 UI 与逻辑。
