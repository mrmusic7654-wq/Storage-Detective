<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Default values -->
    <integer name="default_scan_interval">24</integer>
    <integer name="default_clean_threshold">85</integer>
    <integer name="default_backup_frequency">7</integer>
    <integer name="default_undo_history_days">7</integer>
    <integer name="default_auto_lock_timeout">300</integer>
    
    <!-- Limits -->
    <integer name="max_scan_interval">168</integer>
    <integer name="min_scan_interval">1</integer>
    <integer name="max_clean_threshold">95</integer>
    <integer name="min_clean_threshold">50</integer>
    <integer name="max_backup_frequency">30</integer>
    <integer name="min_backup_frequency">1</integer>
    <integer name="max_undo_history_days">30</integer>
    <integer name="min_undo_history_days">1</integer>
    <integer name="max_auto_lock_timeout">1800</integer>
    <integer name="min_auto_lock_timeout">0</integer>
    
    <!-- Animation durations -->
    <integer name="anim_duration_instant">0</integer>
    <integer name="anim_duration_fast">150</integer>
    <integer name="anim_duration_normal">300</integer>
    <integer name="anim_duration_slow">500</integer>
    <integer name="anim_duration_very_slow">800</integer>
    
    <!-- Progress steps -->
    <integer name="progress_max">100</integer>
    <integer name="progress_steps">20</integer>
    
    <!-- Grid columns -->
    <integer name="grid_columns_portrait">2</integer>
    <integer name="grid_columns_landscape">3</integer>
    <integer name="grid_columns_tablet">4</integer>
    
    <!-- List limits -->
    <integer name="max_suggestions">50</integer>
    <integer name="max_history_items">100</integer>
    <integer name="max_events">1000</integer>
    <integer name="max_files_display">500</integer>
    
    <!-- Chart points -->
    <integer name="chart_max_points">30</integer>
    <integer name="chart_min_points">7</integer>
    
    <!-- Throttle limits -->
    <integer name="throttle_scan_ms">5000</integer>
    <integer name="throttle_update_ms">1000</integer>
    <integer name="throttle_sync_ms">30000</integer>
    
    <!-- Cache limits -->
    <integer name="cache_max_size_mb">100</integer>
    <integer name="cache_max_age_days">7</integer>
    <integer name="thumbnail_cache_size_mb">50</integer>
    
    <!-- Database limits -->
    <integer name="database_max_entries">10000</integer>
    <integer name="database_cleanup_days">30</integer>
    
    <!-- Worker limits -->
    <integer name="worker_max_retries">3</integer>
    <integer name="worker_backoff_delay">30</integer>
    
    <!-- Notification IDs -->
    <integer name="notification_id_scan">1001</integer>
    <integer name="notification_id_clean">1002</integer>
    <integer name="notification_id_warning">1003</integer>
    <integer name="notification_id_critical">1004</integer>
    <integer name="notification_id_backup">1005</integer>
    <integer name="notification_id_sync">1006</integer>
    <integer name="notification_id_achievement">1007</integer>
    
    <!-- Permission request codes -->
    <integer name="permission_request_storage">100</integer>
    <integer name="permission_request_camera">101</integer>
    <integer name="permission_request_notifications">102</integer>
    <integer name="permission_request_manage_storage">103</integer>
    
    <!-- Version codes -->
    <integer name="database_version">5</integer>
    <integer name="app_version_code">1</integer>
    
    <!-- Thread pool sizes -->
    <integer name="thread_pool_size">4</integer>
    <integer name="scan_thread_pool">2</integer>
    <integer name="cleanup_thread_pool">2</integer>
    
    <!-- Batch sizes -->
    <integer name="batch_size_insert">100</integer>
    <integer name="batch_size_delete">50</integer>
    <integer name="batch_size_update">100</integer>
    
    <!-- File size thresholds -->
    <integer name="small_file_threshold_kb">100</integer>
    <integer name="medium_file_threshold_mb">10</integer>
    <integer name="large_file_threshold_mb">100</integer>
    <integer name="huge_file_threshold_gb">1</integer>
    
    <!-- Age thresholds -->
    <integer name="recent_file_days">7</integer>
    <integer name="old_file_days">30</integer>
    <integer name="ancient_file_days">365</integer>
    
    <!-- Game thresholds -->
    <integer name="xp_per_mb">1</integer>
    <integer name="xp_per_duplicate">10</integer>
    <integer name="achievement_bonus_xp">100</integer>
    <integer name="daily_login_xp">50</integer>
    
    <!-- Level thresholds -->
    <integer name="level_1_xp">0</integer>
    <integer name="level_2_xp">1000</integer>
    <integer name="level_3_xp">2500</integer>
    <integer name="level_4_xp">5000</integer>
    <integer name="level_5_xp">10000</integer>
    <integer name="level_6_xp">20000</integer>
    <integer name="level_7_xp">35000</integer>
    <integer name="level_8_xp">55000</integer>
    <integer name="level_9_xp">80000</integer>
    <integer name="level_10_xp">110000</integer>
</resources>
