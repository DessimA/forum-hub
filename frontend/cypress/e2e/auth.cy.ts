describe('Authentication Flow', () => {
  it('should redirect to login when accessing protected route', () => {
    cy.visit('/')
    cy.url().should('include', '/login')
  })

  it('should login successfully with valid credentials', () => {
    cy.visit('/login')
    cy.get('input[type="text"]').type('admin@forumhub.com')
    cy.get('input[type="password"]').type('123456')
    cy.get('button[type="submit"]').click()
    
    cy.url().should('not.include', '/login')
    cy.contains('FórumHub')
  })

  it('should show error with invalid credentials', () => {
    cy.visit('/login')
    cy.get('input[type="text"]').type('wrong@email.com')
    cy.get('input[type="password"]').type('wrong')
    cy.get('button[type="submit"]').click()
    
    cy.contains('E-mail ou senha inválidos')
  })

  it('should logout', () => {
    // Manual login
    cy.visit('/login')
    cy.get('input[type="text"]').type('admin@forumhub.com')
    cy.get('input[type="password"]').type('123456')
    cy.get('button[type="submit"]').click()
    
    cy.visit('/')
    cy.get('.v-avatar').click()
    cy.contains('Sair').click()
    cy.url().should('include', '/login')
  })
})
