#!/usr/bin/env python3
"""
send_fcm.py

Sends an industry-standard data-only FCM push notification via FCM HTTP v1 API.
Supports both GitHub Actions workflow dispatch execution and local command line execution.
"""

import argparse
import json
import os
import sys
import urllib.error
import urllib.request


def build_fcm_payload(
    topic: str,
    title: str,
    body: str,
    channel_id: str = "general",
    launch_url: str = None,
    image_url: str = None,
    icon_url: str = None,
    btn1_text: str = None,
    btn1_url: str = None,
    btn2_text: str = None,
    btn2_url: str = None,
    constraint_min_version: str = None,
    constraint_max_version: str = None,
    constraint_tier: str = None,
    constraint_auth: str = None,
) -> dict:
    data = {
        "title": title.strip(),
        "body": body.strip(),
        "channel_id": (channel_id or "general").strip(),
    }

    if launch_url and launch_url.strip():
        data["launch_url"] = launch_url.strip()
    if image_url and image_url.strip():
        data["image_url"] = image_url.strip()
    if icon_url and icon_url.strip():
        data["icon_url"] = icon_url.strip()

    if btn1_text and btn1_text.strip():
        data["btn1_text"] = btn1_text.strip()
        if btn1_url and btn1_url.strip():
            data["btn1_url"] = btn1_url.strip()

    if btn2_text and btn2_text.strip():
        data["btn2_text"] = btn2_text.strip()
        if btn2_url and btn2_url.strip():
            data["btn2_url"] = btn2_url.strip()

    if constraint_min_version and constraint_min_version.strip():
        data["constraint_min_version"] = constraint_min_version.strip()
    if constraint_max_version and constraint_max_version.strip():
        data["constraint_max_version"] = constraint_max_version.strip()

    if constraint_tier and constraint_tier.strip() and constraint_tier.strip() != "ALL":
        data["constraint_tier"] = constraint_tier.strip()

    if constraint_auth and constraint_auth.strip() and constraint_auth.strip() != "ANY":
        data["constraint_auth"] = constraint_auth.strip()

    clean_topic = topic.strip().lstrip("/topics/")

    return {
        "message": {
            "topic": clean_topic,
            "data": data,
        }
    }


def send_fcm_message(project_id: str, access_token: str, payload: dict) -> dict:
    url = f"https://fcm.googleapis.com/v1/projects/{project_id}/messages:send"
    data_bytes = json.dumps(payload).encode("utf-8")

    req = urllib.request.Request(
        url=url,
        data=data_bytes,
        headers={
            "Authorization": f"Bearer {access_token}",
            "Content-Type": "application/json; UTF-8",
        },
        method="POST",
    )

    try:
        with urllib.request.urlopen(req, timeout=30) as response:
            res_body = response.read().decode("utf-8")
            return json.loads(res_body)
    except urllib.error.HTTPError as e:
        error_msg = e.read().decode("utf-8")
        try:
            parsed_err = json.loads(error_msg)
            print(f"ERROR: Google FCM API returned HTTP {e.code}: {json.dumps(parsed_err, indent=2)}", file=sys.stderr)
        except Exception:
            print(f"ERROR: Google FCM API returned HTTP {e.code}: {error_msg}", file=sys.stderr)
        sys.exit(1)
    except urllib.error.URLError as e:
        print(f"ERROR: Network error connecting to FCM: {e.reason}", file=sys.stderr)
        sys.exit(1)


def write_github_summary(topic: str, payload: dict, response: dict):
    summary_path = os.environ.get("GITHUB_STEP_SUMMARY")
    if not summary_path:
        return

    data = payload.get("message", {}).get("data", {})
    message_id = response.get("name", "N/A")

    rows = [
        "## 🚀 FCM Push Notification Sent Successfully",
        "",
        "| Property | Value |",
        "|---|---|",
        f"| **Message ID** | `{message_id}` |",
        f"| **Target Topic** | `{topic}` |",
        f"| **Title** | {data.get('title', '')} |",
        f"| **Body** | {data.get('body', '')} |",
        f"| **Channel ID** | `{data.get('channel_id', 'general')}` |",
    ]

    if "launch_url" in data:
        rows.append(f"| **Launch URL** | `{data['launch_url']}` |")
    if "image_url" in data:
        rows.append(f"| **Banner Image** | {data['image_url']} |")
    if "icon_url" in data:
        rows.append(f"| **Avatar Icon** | {data['icon_url']} |")
    if "btn1_text" in data:
        rows.append(f"| **Button 1** | `{data['btn1_text']}` &rarr; `{data.get('btn1_url', '')}` |")
    if "btn2_text" in data:
        rows.append(f"| **Button 2** | `{data['btn2_text']}` &rarr; `{data.get('btn2_url', '')}` |")
    if "constraint_min_version" in data:
        rows.append(f"| **Min Version** | `{data['constraint_min_version']}` |")
    if "constraint_max_version" in data:
        rows.append(f"| **Max Version** | `{data['constraint_max_version']}` |")
    if "constraint_tier" in data:
        rows.append(f"| **Target Tier** | `{data['constraint_tier']}` |")
    if "constraint_auth" in data:
        rows.append(f"| **Target Auth** | `{data['constraint_auth']}` |")

    rows.append("")
    rows.append("### Raw FCM Request Payload")
    rows.append("```json")
    rows.append(json.dumps(payload, indent=2, ensure_ascii=False))
    rows.append("```")

    try:
        with open(summary_path, "a", encoding="utf-8") as f:
            f.write("\n".join(rows) + "\n")
    except Exception as e:
        print(f"Warning: Failed to write GITHUB_STEP_SUMMARY: {e}", file=sys.stderr)


def get_token_from_service_account(sa_path: str):
    try:
        from google.oauth2 import service_account
        import google.auth.transport.requests

        scopes = ["https://www.googleapis.com/auth/firebase.messaging"]
        creds = service_account.Credentials.from_service_account_file(sa_path, scopes=scopes)
        auth_req = google.auth.transport.requests.Request()
        creds.refresh(auth_req)
        return creds.token, creds.project_id
    except ImportError:
        print("ERROR: 'google-auth' library is required to parse service account JSON files locally.", file=sys.stderr)
        print("Run: pip install google-auth requests", file=sys.stderr)
        sys.exit(1)


def main():
    parser = argparse.ArgumentParser(description="Send industry-standard FCM Data Notification via HTTP v1")
    parser.add_argument("--project-id", help="Firebase Project ID")
    parser.add_argument("--access-token", help="OAuth2 Access Token for Firebase Cloud Messaging")
    parser.add_argument("--service-account", help="Path to Firebase Service Account JSON file (for local run)")
    parser.add_argument("--topic", default="all", help="Target topic (default: all)")
    parser.add_argument("--title", required=False, help="Notification title")
    parser.add_argument("--body", required=False, help="Notification body")
    parser.add_argument("--channel-id", default="general", help="Notification channel (general | promotions)")
    parser.add_argument("--launch-url", help="Target deep link or web URL")
    parser.add_argument("--image-url", help="Big picture banner URL")
    parser.add_argument("--icon-url", help="Avatar icon URL")
    parser.add_argument("--btn1-text", help="Button 1 label")
    parser.add_argument("--btn1-url", help="Button 1 URL / deep link")
    parser.add_argument("--btn2-text", help="Button 2 label")
    parser.add_argument("--btn2-url", help="Button 2 URL / deep link")
    parser.add_argument("--constraint-min-version", help="Min app version code")
    parser.add_argument("--constraint-max-version", help="Max app version code")
    parser.add_argument("--constraint-tier", default="ALL", help="Target tier (ALL | PREMIUM_ONLY | FREE_ONLY)")
    parser.add_argument("--constraint-auth", default="ANY", help="Target auth (ANY | LOGGED_IN_ONLY | LOGGED_OUT_ONLY)")

    args = parser.parse_args()

    # Determine Project ID and Access Token
    project_id = args.project_id or os.environ.get("PROJECT_ID")
    access_token = args.access_token or os.environ.get("ACCESS_TOKEN")

    sa_path = args.service_account or os.environ.get("GOOGLE_APPLICATION_CREDENTIALS")
    if (not access_token or not project_id) and sa_path:
        print(f"Authenticating using service account key: {sa_path}")
        minted_token, sa_project_id = get_token_from_service_account(sa_path)
        access_token = access_token or minted_token
        project_id = project_id or sa_project_id

    if not project_id:
        # Default fallback project ID from StoryGhost google-services.json
        project_id = "story-ghost-0"

    if not access_token:
        print("ERROR: Missing access token or service account credentials.", file=sys.stderr)
        print("Provide either ACCESS_TOKEN environment variable or --service-account argument.", file=sys.stderr)
        sys.exit(1)

    # Read payload fields from CLI arguments or environment variables
    topic = args.topic or os.environ.get("FCM_TOPIC") or "all"
    title = args.title or os.environ.get("FCM_TITLE")
    body = args.body or os.environ.get("FCM_BODY")
    channel_id = args.channel_id or os.environ.get("FCM_CHANNEL_ID") or "general"
    launch_url = args.launch_url or os.environ.get("FCM_LAUNCH_URL")
    image_url = args.image_url or os.environ.get("FCM_IMAGE_URL")
    icon_url = args.icon_url or os.environ.get("FCM_ICON_URL")
    btn1_text = args.btn1_text or os.environ.get("FCM_BTN1_TEXT")
    btn1_url = args.btn1_url or os.environ.get("FCM_BTN1_URL")
    btn2_text = args.btn2_text or os.environ.get("FCM_BTN2_TEXT")
    btn2_url = args.btn2_url or os.environ.get("FCM_BTN2_URL")
    constraint_min_version = args.constraint_min_version or os.environ.get("FCM_CONSTRAINT_MIN_VERSION")
    constraint_max_version = args.constraint_max_version or os.environ.get("FCM_CONSTRAINT_MAX_VERSION")
    constraint_tier = args.constraint_tier or os.environ.get("FCM_CONSTRAINT_TIER") or "ALL"
    constraint_auth = args.constraint_auth or os.environ.get("FCM_CONSTRAINT_AUTH") or "ANY"

    if not title or not body:
        print("ERROR: Notification 'title' and 'body' are required.", file=sys.stderr)
        sys.exit(1)

    payload = build_fcm_payload(
        topic=topic,
        title=title,
        body=body,
        channel_id=channel_id,
        launch_url=launch_url,
        image_url=image_url,
        icon_url=icon_url,
        btn1_text=btn1_text,
        btn1_url=btn1_url,
        btn2_text=btn2_text,
        btn2_url=btn2_url,
        constraint_min_version=constraint_min_version,
        constraint_max_version=constraint_max_version,
        constraint_tier=constraint_tier,
        constraint_auth=constraint_auth,
    )

    print("========================================")
    print("Preparing FCM HTTP v1 Notification")
    print(f"Project ID: {project_id}")
    print(f"Topic:      {topic}")
    print(f"Payload:\n{json.dumps(payload, indent=2, ensure_ascii=False)}")
    print("========================================")

    print("Sending message to FCM API...")
    res = send_fcm_message(project_id=project_id, access_token=access_token, payload=payload)
    print(f"SUCCESS: Message delivered! Response: {json.dumps(res, indent=2)}")

    write_github_summary(topic=topic, payload=payload, response=res)


if __name__ == "__main__":
    main()
