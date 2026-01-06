describe('Mobile Navigation', () => {
  beforeEach(() => {
    cy.viewport(375, 667) // iPhone SE
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
  })
  
  it('should show hamburger menu and functional drawer', () => {
    cy.get('[data-cy="hamburger-menu"]').should('be.visible').click()
    cy.get('[data-cy="nav-drawer"]').should('be.visible')
    cy.get('[data-cy="nav-drawer"]').contains('Explorar Tópicos').click({ force: true })
    cy.url().should('include', '/topics')
    cy.get('[data-cy="nav-drawer"]').should('not.be.visible')
  })
  
  it('logo should show animated version', () => {
    cy.get('[data-cy="navbar-logo"]').within(() => {
      cy.get('.animated-logo').should('be.visible')
    })
  })
})