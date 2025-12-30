# 11 - Backend/Frontend Integration Analysis

This document details Backend functionalities that are currently underutilized or ignored by the Frontend interface, representing UX improvement opportunities with zero API development cost.

## 🔍 Optimization Opportunities

### 1. Topic Filtering and Search (Critical)
- **Backend:** Optimized database structure with indexes on `status`, `course_id`, and `author_id`. Supports efficient queries.
- **Current Frontend:** Displays a single paginated list with no filter options.
- **Untapped Potential:**
  - **Course Filter:** Users cannot view only "Java" or "Vue.js" questions, even though the course listing endpoint exists.
  - **Status Filter:** No way to see only "Unresolved" topics, making it hard for instructors to find where to help.
  - **Text Search:** Database supports `LIKE` on title, but there is no search bar in the UI.

### 2. Dynamic Sorting
- **Backend:** Native Spring Data Pageable support for `?sort=field,desc` parameters.
- **Current Frontend:** Fixed order (likely insertion).
- **Untapped Potential:**
  - Add dropdown: "Newest", "Oldest", "By Status".
  - Allow users to control content relevance.

### 3. Validation Feedback and UX
- **Backend:** Rich validation (`@NotBlank`, duplication check, role hierarchy rules).
- **Current Frontend:** Uses native browser `alert()` and `prompt()` for critical actions (e.g., User blocking, Log deletion).
- **Untapped Potential:**
  - Replace `prompt` with Vuetify Dialogs (`v-dialog`) for elegant justification capture.
  - Display Backend validation errors (e.g., "Duplicate topic") directly on form fields (`v-text-field :error-messages`) instead of generic alerts.

### 4. Blocking Management (Moderator)
- **Backend:** Allows temporary blocking with exact expiration date.
- **Current Frontend:** Asks for "days" via simple prompt.
- **Untapped Potential:**
  - Interface with `Date Picker` to select the exact unblock date, leveraging the `blocked_until` database field.

## 🎯 Immediate Recommendation
Prioritize implementing the **Filter Bar in Topic Listing** (`TopicListView`). This will drastically increase system usability using endpoints that already exist and are tested.
