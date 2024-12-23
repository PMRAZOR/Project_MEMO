export default [
  {
    path: '/boards',
    name: 'BoardList',
    component: () => import('@/views/board/BoardListView.vue'),
    meta: { title: '게시판' }
  },
  {
    path: '/boards/create',
    name: 'BoardCreate',
    component: () => import('@/views/board/BoardCreateView.vue'),
    meta: { title: '게시글 작성', requiresAuth: true }
  },
  {
    path: '/boards/:id',
    name: 'BoardDetail',
    component: () => import('@/views/board/BoardDetailView.vue'),
    meta: { title: '게시글 상세' }
  },
  {
    path: '/boards/:id/edit',
    name: 'BoardEdit',
    component: () => import('@/views/board/BoardEditView.vue'),
    meta: { title: '게시글 수정', requiresAuth: true }
  }
];