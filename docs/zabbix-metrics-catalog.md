# Zabbix Metrics Catalog (GSIS UserId Mapper)

This document lists all metrics currently emitted by the GSIS UserId mapper and the exact tag combinations that can appear based on code paths.

Scope:
- Mapper: GsisUserIdProviderMapper
- Endpoint tag value: tax_id_converter

## 1) Metric: gsis_userid_api_failures_total
Tags:
- mapper
- endpoint
- client_type
- failure_type

Expected tag values:
- mapper: GsisUserIdProviderMapper
- endpoint: tax_id_converter
- client_type: secure, simple, unknown
- failure_type:
  - timeout
  - http_non_200
  - connection
  - tls
  - parse_error
  - empty_response
  - missing_gov_user_id
  - unknown

Emitted combinations (Cartesian product):
- mapper=GsisUserIdProviderMapper
- endpoint=tax_id_converter
- client_type in {secure, simple, unknown}
- failure_type in {timeout, http_non_200, connection, tls, parse_error, empty_response, missing_gov_user_id, unknown}

Total explicit combinations: 3 x 8 = 24

## 2) Metric: gsis_userid_api_timeouts_total
Tags:
- mapper
- endpoint
- client_type

Expected tag values:
- mapper: GsisUserIdProviderMapper
- endpoint: tax_id_converter
- client_type: secure, simple, unknown

Emitted combinations:
- (GsisUserIdProviderMapper, tax_id_converter, secure)
- (GsisUserIdProviderMapper, tax_id_converter, simple)
- (GsisUserIdProviderMapper, tax_id_converter, unknown)

Total explicit combinations: 3

## 3) Metric: gsis_userid_api_http_status_total
Tags:
- mapper
- endpoint
- client_type
- status_code
- status_class

Expected tag values:
- mapper: GsisUserIdProviderMapper
- endpoint: tax_id_converter
- client_type: secure, simple, unknown
- status_code: any integer >= 400 that was observed during failed external API calls
- status_class: 4xx or 5xx

Emitted combinations:
- mapper=GsisUserIdProviderMapper
- endpoint=tax_id_converter
- client_type in {secure, simple, unknown}
- status_code in observed failed HTTP codes (>= 400)
- status_class derived from status_code:
  - 400-499 -> 4xx
  - 500-599 -> 5xx

Note:
- status_class is derived from status_code.
- This is an open set because status_code depends on runtime responses.

## 4) Metric: gsis_userid_auth_failures_total
Tags:
- mapper
- reason

Expected tag values:
- mapper: GsisUserIdProviderMapper
- reason:
  - missing_converter_url
  - missing_taxid
  - external_api_no_userid
  - missing_user_attribute_config

Emitted combinations:
- (GsisUserIdProviderMapper, missing_converter_url)
- (GsisUserIdProviderMapper, missing_taxid)
- (GsisUserIdProviderMapper, external_api_no_userid)
- (GsisUserIdProviderMapper, missing_user_attribute_config)

Total explicit combinations: 4

## 5) Metric: gsis_userid_response_validation_failures_total
Tags:
- mapper
- reason

Expected tag values:
- mapper: GsisUserIdProviderMapper
- reason:
  - response_not_array
  - response_empty
  - gov_user_id_missing

Emitted combinations:
- (GsisUserIdProviderMapper, response_not_array)
- (GsisUserIdProviderMapper, response_empty)
- (GsisUserIdProviderMapper, gov_user_id_missing)

Total explicit combinations: 3

## 6) Metric: gsis_userid_http_client_mode_total
Tags:
- mapper
- endpoint
- mode
- reason

Expected tag values:
- mapper: GsisUserIdProviderMapper
- endpoint: tax_id_converter
- mode/reason pairs:
  - mode=simple, reason=cert_config_missing
  - mode=secure, reason=secure_ok
  - mode=simple, reason=secure_init_failed

Emitted combinations:
- (GsisUserIdProviderMapper, tax_id_converter, simple, cert_config_missing)
- (GsisUserIdProviderMapper, tax_id_converter, secure, secure_ok)
- (GsisUserIdProviderMapper, tax_id_converter, simple, secure_init_failed)

Total explicit combinations: 3

## Legacy metric (still present)
Metric: gsis_metrics_counter
Tags: none

Purpose:
- Generic counter, not used for alert logic in current mapper flow.

## Practical recommendation for Zabbix triggers
Use these metrics for alerts:
- gsis_userid_api_failures_total (primary)
- gsis_userid_api_timeouts_total (timeout-specific)
- gsis_userid_api_http_status_total (4xx/5xx split)
- gsis_userid_response_validation_failures_total (API contract/schema issues)
- gsis_userid_http_client_mode_total (secure client regression/fallback)

Keep gsis_userid_auth_failures_total as a business impact signal.

## Example Zabbix alerts for gsis_userid_api_failures_total
The examples below classify alerts into info/warning/critical severity levels.

Assumptions:
- You ingest Prometheus metrics into Zabbix and expose a numeric item per label set.
- Trigger examples use rate of new failures with `change()` over a time window.
- Replace `<item_key_for_failure_series>` with your actual item key for each label combination.

Suggested item naming convention:
- Metric: gsis_userid_api_failures_total
- Labels: mapper=GsisUserIdProviderMapper, endpoint=tax_id_converter, client_type=*, failure_type=*

### Info level
Use info to detect isolated or low-rate failures that are not yet service-impacting.

Example trigger expression:
```
change(/<host>/<item_key_for_failure_series>,5m) > 0
```

Recommended scope:
- failure_type in {parse_error, empty_response, unknown}

Example name:
- [INFO] GSIS UserId API occasional failures detected ({ITEM.LASTVALUE})

### Warning level
Use warning to detect persistent failure growth over a short interval.

Example trigger expression:
```
change(/<host>/<item_key_for_failure_series>,10m) >= 5
```

Recommended scope:
- failure_type in {timeout, connection, tls, http_non_200}
- Separate triggers per failure_type and client_type for faster triage

Example name:
- [WARNING] GSIS UserId API failure burst in last 10m ({ITEM.LASTVALUE})

### Critical level
Use critical to detect sustained or high-volume failures indicating likely outage or severe degradation.

Example trigger expressions:
```
change(/<host>/<item_key_for_failure_series>,15m) >= 20
```

```
change(/<host>/<item_key_for_failure_series>,5m) >= 10
and
change(/<host>/<item_key_for_failure_series>,5m:now-5m) >= 10
```

Recommended scope:
- failure_type in {timeout, connection, tls}
- client_type=secure (higher operational impact when secure path fails repeatedly)

Example name:
- [CRITICAL] GSIS UserId API sustained failure storm (possible external API outage)

### Optional anti-noise guidance
- Add dependency so warning depends on critical to avoid duplicate incidents.
- Add recovery hysteresis, for example require `change(...,10m) = 0` before closing.
- Keep separate triggers for network-related failures and payload/validation failures.
