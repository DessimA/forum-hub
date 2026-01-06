describe('Admin Management', () => {
  beforeEach(() => {
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
    // Wait for initial load
    cy.get('[data-cy="topic-card"]', { timeout: 30000 }).should('have.length.at.least', 1)
  })

  it('should access admin users page', () => {
    // Open hamburger menu
    cy.get('[data-cy="hamburger-menu"]').should('be.visible').click({ force: true })
    
    // Wait for drawer to exist
    cy.get('[data-cy="nav-drawer"]', { timeout: 15000 }).should('exist')
    
    // Interact with drawer items
    cy.get('[data-cy="nav-drawer"]')
      .contains('Gestão de Usuários')
      .scrollIntoView()
      .should('exist')
      .click({ force: true })
    
    cy.url().should('include', '/admin/users')
    cy.contains('h1', 'Gestão de Usuários').should('be.visible')
  })

  it('should access audit logs', () => {
    cy.get('[data-cy="hamburger-menu"]').should('be.visible').click({ force: true })
    
    cy.get('[data-cy="nav-drawer"]', { timeout: 15000 }).should('exist')
    
    cy.get('[data-cy="nav-drawer"]')
      .contains('Auditoria')
      .scrollIntoView()
      .should('exist')
      .click({ force: true })
    
    cy.url().should('include', '/admin/logs')
    cy.contains('h1', 'Auditoria').should('be.visible')
  })
})
