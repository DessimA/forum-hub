describe('Mobile Filters Stack', () => {
  beforeEach(() => {
    cy.viewport(375, 667)
    cy.login('admin@forumhub.com', 'admin123')
    cy.visit('/topics')
    // Wait for the main content to ensure layout is ready
    cy.contains('h1', 'Explorar Tópicos', { timeout: 30000 }).should('be.visible')
  })
  
  it('filters should stack and be visible', () => {
    // Scroll to ensure filters are in view if needed
    cy.get('[data-cy="filter-search"]').scrollIntoView().should('be.visible')
    cy.get('[data-cy="filter-status"]').should('be.visible')
    cy.get('[data-cy="filter-curso"]').should('be.visible')
    
    // Check that search is full width (taking padding into account, > 300px is safe for 375px width)
    cy.get('[data-cy="filter-search"]').invoke('width').should('be.gt', 300)
  })
  
  it('fields should have touch-friendly height (>=36px for filters)', () => {
    cy.get('[data-cy="filter-search"]').scrollIntoView().invoke('outerHeight').should('be.gte', 36)
  })
})