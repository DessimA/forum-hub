/// <reference types="cypress" />

Cypress.Commands.add('login', (email, password) => {
  const apiUrl = Cypress.env('API_URL') || 'http://localhost:8080'
  cy.request({
    method: 'POST',
    url: `${apiUrl}/api/v1/auth/login`,
    body: { email, password },
    failOnStatusCode: false
  }).then((response) => {
    if (response.status === 200) {
      localStorage.setItem('token', response.body.token)
    }
  })
})

Cypress.Commands.add('setTheme', (theme) => {
  localStorage.setItem('theme', theme)
})

Cypress.Commands.add('checkResponsive', (viewport: 'mobile' | 'desktop') => {
  if (viewport === 'mobile') {
    cy.viewport(375, 667)
    cy.get('[data-cy="nav-drawer-toggle"]').should('be.visible')
  } else {
    cy.viewport(1280, 720)
    cy.get('.v-app-bar').contains('Novo Tópico').should('be.visible')
  }
})

declare global {
  namespace Cypress {
    interface Chainable {
      login(email: string, password: string): Chainable<void>
      setTheme(theme: 'light' | 'dark'): Chainable<void>
      checkResponsive(viewport: 'mobile' | 'desktop'): Chainable<void>
    }
  }
}
