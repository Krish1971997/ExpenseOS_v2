# ExpenseOS v2 isolated test build

This working tree contains the separate shell/navigation implementation described in the attached chat. It is intended to be tested side-by-side with the existing app; the existing `com.expenseos` install is not replaced by the debug build.

## Build identity

- Namespace and Java package remain `com.expenseos`.
- Debug `applicationId` is `com.expenseos.v2`; launcher label is `ExpenseOS v2`.
- Debug OAuth callback is `expenseosv2://oauth`.
- Release identity remains `com.expenseos` for a later, deliberate merge/release decision.

Before testing Zoho login, add `expenseosv2://oauth` as an authorized redirect URI in the Zoho API Console. If the existing Zoho client cannot authorize both schemes, use a separate Zoho mobile client and configure its credentials for this test build; do not change the existing app's callback registration without checking the old login first.

## Data safety

The v2 app has its own local database, preferences, keystore entries, and Zoho session. However, this code still connects to the existing cloud backend when configured. Before opening v2 against real data, make a cloud backup, avoid editing/deleting production transactions, and disable scheduled automation/notifications/alarms for the v2 installation during testing. Google Calendar OAuth may need a separate Android client registration for package `com.expenseos.v2` and the debug signing SHA-1.

## Run

Open this folder in Android Studio, allow Gradle sync, select the `app` debug variant, and Run. The initial route uses the book list if there is no valid active book; otherwise it opens the new shell. Verify the top bar, book switcher, four tabs, More routes, restore confirmation, sign-out, rotation/back behavior, and switching/deleting the active book.

## Validation performed in the sandbox

The project was cloned to a separate directory and edited on branch `claude-chat-phase1`; no upstream changes were pushed or merged. Resource/file reference checks passed. An attempted `assembleDebug testDebugUnitTest` could not start compilation because this sandbox has no Android SDK installed (`SDK location not found`). Therefore an APK build and on-device login/navigation tests remain to be completed in Android Studio with API 34 SDK installed.
