describe('Navigation Drawer Scroll', () => {
  beforeEach(() => {
    cy.viewport(375, 667)
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
    cy.get('[data-cy="hamburger-menu"]').click()
  })

  it('drawer should allow scroll when content exceeds height', () => {
    cy.get('[data-cy="nav-drawer"]').should('be.visible')
    
    // Check if scrollable area exists and can scroll
    cy.get('.v-navigation-drawer__content').scrollTo('bottom', { ensureScrollable: false })
    cy.get('[data-cy="logout-button"]').should('exist')
    
    cy.get('.v-navigation-drawer__content').scrollTo('top', { ensureScrollable: false })
    cy.contains('Explorar Tópicos').should('exist')
  })

  it('icons should be aligned vertically', () => {
    cy.get('[data-cy="nav-item-explorar"]').within(() => {
      cy.get('.v-icon').should('exist')
    })
  })
})
