# WhatsApp Chatlytic

**WhatsApp Chatlytic** is an Android app that analyzes exported WhatsApp chat archives and generates smart insights such as top-used words, user activity, and sentiment — powered by AI.

The app can connect to **OpenAI GPT models (or any other AI API)** to perform advanced text understanding, summarization, and conversational analytics on chat data.

---

## Features

- Import WhatsApp chat exports (`.txt` files)
-  Automatic statistics:
  - Most frequent words & message counts  
  - Most active participants  
  - Activity by time & date  
-  **AI Integration**:
  - Sends requests to GPT or another AI API  
  - Generates conversation summaries, tone analysis, or topic clustering  
  - Suggests smart insights (“You were most active in the evenings discussing work-related topics”)  
- Interactive charts and reports  
-  Option to export summaries and analytics

---

## How It Works

1. The user exports a chat from WhatsApp (`.txt` format).  
2. The app parses the text and extracts message metadata (dates, senders, messages).  
3. Key metrics are calculated locally.  
4. (Optional) The user enables **AI mode**, and the app:
   - Sends selected chat content or summaries via **API requests** (e.g., OpenAI GPT API)  
   - Receives structured insights or summaries from the AI  
   - Displays them in a clean, visual dashboard

---
