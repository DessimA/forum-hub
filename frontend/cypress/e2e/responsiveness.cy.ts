describe('Responsiveness', () => {
  beforeEach(() => {
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
    // Wait for content to load - significant timeout for potential backend/network lag in CI
    cy.get('[data-cy="topic-card"]', { timeout: 30000 }).should('have.length.at.least', 1)
  })

  it('should show mobile menu on small screens', () => {
    cy.viewport(375, 667)
    cy.get('[data-cy="hamburger-menu"]').should('be.visible').click({ force: true })
    // Drawer might take a moment to animate
    cy.get('[data-cy="nav-drawer"]', { timeout: 10000 }).should('exist')
    cy.get('[data-cy="nav-drawer"]').contains('Explorar Tópicos').should('be.visible')
  })

  it('should show desktop navigation on large screens', () => {
    cy.viewport(1440, 900)
    // On desktop, the bar should contain the text
    cy.get('.v-app-bar').contains('Novo Tópico').should('be.visible')
  })

  it('should adapt topic grid based on viewport', () => {
    // Desktop: lg="4" (3 columns)
    cy.viewport(1920, 1080)
    // We check for the class presence
    cy.get('.v-row').find('.v-col-lg-4', { timeout: 10000 }).should('exist')

    // Mobile: default 12 (1 column)
    cy.viewport(375, 667)
    cy.get('.v-row').find('.v-col-12', { timeout: 10000 }).should('exist')
  })
})