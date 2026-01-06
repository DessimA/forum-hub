describe('Mobile Filters Stack', () => {
  beforeEach(() => {
    cy.viewport(375, 667)
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
  })
  
  it('filters should stack and be visible', () => {
    cy.get('[data-cy="filter-search"]').should('be.visible')
    cy.get('[data-cy="filter-status"]').should('be.visible')
    cy.get('[data-cy="filter-curso"]').should('be.visible')
    
    // Check that search is full width
    cy.get('[data-cy="filter-search"]').invoke('width').should('be.gt', 300)
  })
  
  it('fields should have touch-friendly height (>=36px for filters)', () => {
    // Filter fields are compact, height around 40px in Vuetify
    cy.get('[data-cy="filter-search"]').invoke('outerHeight').should('be.gte', 36)
  })
})
