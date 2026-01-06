describe('Authentication Flow', () => {
  it('should show landing page for unauthenticated user on /', () => {
    cy.visit('/')
    cy.contains('Conectando conhecimento').should('be.visible')
  })

  it('should redirect to login when accessing protected route', () => {
    cy.visit('/topics')
    cy.url().should('include', '/login')
  })

  it('should login successfully with valid credentials', () => {
    cy.visit('/login')
    cy.get('[data-cy="login-email"]').find('input').type('admin@forumhub.com')
    cy.get('[data-cy="login-password"]').find('input').type('admin123')
    cy.get('[data-cy="login-submit"]').click()
    
    cy.url().should('include', '/topics')
    cy.get('[data-cy="user-avatar"]').should('be.visible')
  })

  it('should redirect authenticated user from landing to topics', () => {
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/')
    cy.url().should('include', '/topics')
  })

  it('should logout and go back to landing', () => {
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
    
    // Open drawer
    cy.get('[data-cy="user-avatar"]').should('be.visible').click({ force: true })
    
    // Ensure drawer is open and click logout
    cy.get('[data-cy="logout-button"]', { timeout: 10000 })
      .scrollIntoView()
      .should('exist')
      .click({ force: true })
    
    cy.contains('Conectando conhecimento', { timeout: 20000 }).should('be.visible')
    cy.url().should('match', /\/$/) 
  })
})