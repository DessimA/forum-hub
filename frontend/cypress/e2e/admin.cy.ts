describe('Admin Management', () => {
  beforeEach(() => {
    cy.visit('/login')
    cy.get('input[type="text"]').type('admin@forumhub.com')
    cy.get('input[type="password"]').type('123456')
    cy.get('button[type="submit"]').click()
  })

  it('should access admin users page', () => {
    cy.get('.v-avatar').click()
    cy.contains('Gestão Usuários').click()
    cy.url().should('include', '/admin/users')
  })

  it('should access audit logs', () => {
    cy.get('.v-avatar').click()
    cy.contains('Auditoria').click()
    cy.url().should('include', '/admin/logs')
  })
})
