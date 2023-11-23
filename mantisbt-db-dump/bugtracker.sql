-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: mysql
-- Creato il: Ott 20, 2023 alle 10:56
-- Versione del server: 5.7.41
-- Versione PHP: 8.1.19

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bugtracker`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_api_token_table`
--

CREATE TABLE `mantis_api_token_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `name` varchar(128) NOT NULL,
  `hash` varchar(128) NOT NULL,
  `date_created` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `date_used` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_bugnote_table`
--

CREATE TABLE `mantis_bugnote_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `bug_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `reporter_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `bugnote_text_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `view_state` smallint(6) NOT NULL DEFAULT '10',
  `note_type` int(11) DEFAULT '0',
  `note_attr` varchar(250) DEFAULT '',
  `time_tracking` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `last_modified` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `date_submitted` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_bugnote_text_table`
--

CREATE TABLE `mantis_bugnote_text_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `note` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_bug_file_table`
--

CREATE TABLE `mantis_bug_file_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `bug_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `title` varchar(250) NOT NULL DEFAULT '',
  `description` varchar(250) NOT NULL DEFAULT '',
  `diskfile` varchar(250) NOT NULL DEFAULT '',
  `filename` varchar(250) NOT NULL DEFAULT '',
  `folder` varchar(250) NOT NULL DEFAULT '',
  `filesize` int(11) NOT NULL DEFAULT '0',
  `file_type` varchar(250) NOT NULL DEFAULT '',
  `content` longblob,
  `date_added` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_bug_history_table`
--

CREATE TABLE `mantis_bug_history_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `bug_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `field_name` varchar(64) NOT NULL,
  `old_value` varchar(255) NOT NULL,
  `new_value` varchar(255) NOT NULL,
  `type` smallint(6) NOT NULL DEFAULT '0',
  `date_modified` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_bug_history_table`
--

INSERT INTO `mantis_bug_history_table` (`id`, `user_id`, `bug_id`, `field_name`, `old_value`, `new_value`, `type`, `date_modified`) VALUES
(22, 6, 5, '', '', '', 1, 1681902759),
(23, 6, 5, 'status', '10', '50', 0, 1681902759),
(24, 6, 5, 'handler_id', '0', '8', 0, 1681902759),
(31, 1, 5, '', '1', '6', 18, 1681918168),
(35, 1, 5, '', '1', '6', 19, 1681918181),
(59, 6, 9, '', '', '', 1, 1691925012),
(60, 6, 9, 'status', '10', '50', 0, 1691925012),
(61, 6, 9, 'handler_id', '0', '6', 0, 1691925012);

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_bug_monitor_table`
--

CREATE TABLE `mantis_bug_monitor_table` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `bug_id` int(10) UNSIGNED NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_bug_relationship_table`
--

CREATE TABLE `mantis_bug_relationship_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `source_bug_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `destination_bug_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `relationship_type` smallint(6) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_bug_revision_table`
--

CREATE TABLE `mantis_bug_revision_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `bug_id` int(10) UNSIGNED NOT NULL,
  `bugnote_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `user_id` int(10) UNSIGNED NOT NULL,
  `type` int(10) UNSIGNED NOT NULL,
  `value` longtext NOT NULL,
  `timestamp` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_bug_table`
--

CREATE TABLE `mantis_bug_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `project_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `reporter_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `handler_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `duplicate_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `priority` smallint(6) NOT NULL DEFAULT '30',
  `severity` smallint(6) NOT NULL DEFAULT '50',
  `reproducibility` smallint(6) NOT NULL DEFAULT '10',
  `status` smallint(6) NOT NULL DEFAULT '10',
  `resolution` smallint(6) NOT NULL DEFAULT '10',
  `projection` smallint(6) NOT NULL DEFAULT '10',
  `eta` smallint(6) NOT NULL DEFAULT '10',
  `bug_text_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `os` varchar(32) NOT NULL DEFAULT '',
  `os_build` varchar(32) NOT NULL DEFAULT '',
  `platform` varchar(32) NOT NULL DEFAULT '',
  `version` varchar(64) NOT NULL DEFAULT '',
  `fixed_in_version` varchar(64) NOT NULL DEFAULT '',
  `build` varchar(32) NOT NULL DEFAULT '',
  `profile_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `view_state` smallint(6) NOT NULL DEFAULT '10',
  `summary` varchar(128) NOT NULL DEFAULT '',
  `sponsorship_total` int(11) NOT NULL DEFAULT '0',
  `sticky` tinyint(4) NOT NULL DEFAULT '0',
  `target_version` varchar(64) NOT NULL DEFAULT '',
  `category_id` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `date_submitted` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `due_date` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `last_updated` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_bug_table`
--

INSERT INTO `mantis_bug_table` (`id`, `project_id`, `reporter_id`, `handler_id`, `duplicate_id`, `priority`, `severity`, `reproducibility`, `status`, `resolution`, `projection`, `eta`, `bug_text_id`, `os`, `os_build`, `platform`, `version`, `fixed_in_version`, `build`, `profile_id`, `view_state`, `summary`, `sponsorship_total`, `sticky`, `target_version`, `category_id`, `date_submitted`, `due_date`, `last_updated`) VALUES
(5, 4, 6, 8, 0, 40, 40, 10, 50, 10, 10, 10, 5, 'Windows', '10', 'PC', '', '', '', 1, 10, 'username con _ non riescono a loggare', 0, 0, '', 6, 1681902759, 1, 1681918181),
(9, 8, 6, 6, 0, 20, 20, 50, 50, 10, 10, 10, 9, 'Windows', '10', 'PC', '', '', '', 1, 10, 'Quando chiedi la stampa dei fogli excel crasha', 0, 0, '', 1, 1691925012, 1, 1691925012);

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_bug_tag_table`
--

CREATE TABLE `mantis_bug_tag_table` (
  `bug_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `tag_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `date_attached` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_bug_text_table`
--

CREATE TABLE `mantis_bug_text_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `description` longtext NOT NULL,
  `steps_to_reproduce` longtext NOT NULL,
  `additional_information` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_bug_text_table`
--

INSERT INTO `mantis_bug_text_table` (`id`, `description`, `steps_to_reproduce`, `additional_information`) VALUES
(5, 'username con _ non riescono a loggare', '', ''),
(9, 'Quando chiedi la stampa dei fogl excel crasha', '', '');

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_category_table`
--

CREATE TABLE `mantis_category_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `project_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `name` varchar(128) NOT NULL DEFAULT '',
  `status` int(10) UNSIGNED NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_category_table`
--

INSERT INTO `mantis_category_table` (`id`, `project_id`, `user_id`, `name`, `status`) VALUES
(1, 0, 0, 'General', 0),
(3, 0, 6, 'Front-End', 0),
(4, 0, 6, 'Back-End', 0),
(5, 0, 7, 'Mobile', 0),
(6, 4, 0, 'Auth2', 0);

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_config_table`
--

CREATE TABLE `mantis_config_table` (
  `config_id` varchar(64) NOT NULL,
  `project_id` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `access_reqd` int(11) DEFAULT '0',
  `type` int(11) DEFAULT '90',
  `value` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_config_table`
--

INSERT INTO `mantis_config_table` (`config_id`, `project_id`, `user_id`, `access_reqd`, `type`, `value`) VALUES
('database_version', 0, 0, 90, 1, '209');

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_custom_field_project_table`
--

CREATE TABLE `mantis_custom_field_project_table` (
  `field_id` int(11) NOT NULL DEFAULT '0',
  `project_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `sequence` smallint(6) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_custom_field_string_table`
--

CREATE TABLE `mantis_custom_field_string_table` (
  `field_id` int(11) NOT NULL DEFAULT '0',
  `bug_id` int(11) NOT NULL DEFAULT '0',
  `value` varchar(255) NOT NULL DEFAULT '',
  `text` longtext
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_custom_field_table`
--

CREATE TABLE `mantis_custom_field_table` (
  `id` int(11) NOT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `type` smallint(6) NOT NULL DEFAULT '0',
  `possible_values` text,
  `default_value` varchar(255) NOT NULL DEFAULT '',
  `valid_regexp` varchar(255) NOT NULL DEFAULT '',
  `access_level_r` smallint(6) NOT NULL DEFAULT '0',
  `access_level_rw` smallint(6) NOT NULL DEFAULT '0',
  `length_min` int(11) NOT NULL DEFAULT '0',
  `length_max` int(11) NOT NULL DEFAULT '0',
  `require_report` tinyint(4) NOT NULL DEFAULT '0',
  `require_update` tinyint(4) NOT NULL DEFAULT '0',
  `display_report` tinyint(4) NOT NULL DEFAULT '0',
  `display_update` tinyint(4) NOT NULL DEFAULT '1',
  `require_resolved` tinyint(4) NOT NULL DEFAULT '0',
  `display_resolved` tinyint(4) NOT NULL DEFAULT '0',
  `display_closed` tinyint(4) NOT NULL DEFAULT '0',
  `require_closed` tinyint(4) NOT NULL DEFAULT '0',
  `filter_by` tinyint(4) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_email_table`
--

CREATE TABLE `mantis_email_table` (
  `email_id` int(10) UNSIGNED NOT NULL,
  `email` varchar(64) NOT NULL DEFAULT '',
  `subject` varchar(250) NOT NULL DEFAULT '',
  `metadata` longtext NOT NULL,
  `body` longtext NOT NULL,
  `submitted` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_email_table`
--

INSERT INTO `mantis_email_table` (`email_id`, `email`, `subject`, `metadata`, `body`, `submitted`) VALUES
(30, 'CristianoRusso@gmail.com', '[Contabilità 0000004]: Le fatture sono errate', 'a:3:{s:7:\"headers\";a:0:{}s:7:\"charset\";s:5:\"utf-8\";s:8:\"hostname\";s:9:\"localhost\";}', 'A NOTE has been added to this issue.\n\n---------------------------------------------------------------------- \n (0000007) Miranda23 (updater) - 2023-08-13 13:09\n http://localhost:8989/view.php?id=4#c7 \n---------------------------------------------------------------------- \ncom\'è successo?\n----------------------------------------------------------------------', 1691924995),
(31, 'noctino.misterioso@gmail.com', '[Contabilità 0000004]: Le fatture sono errate', 'a:3:{s:7:\"headers\";a:0:{}s:7:\"charset\";s:5:\"utf-8\";s:8:\"hostname\";s:9:\"localhost\";}', 'A NOTE has been added to this issue.\n\n---------------------------------------------------------------------- \n (0000007) Miranda23 (updater) - 2023-08-13 13:09\n http://localhost:8989/view.php?id=4#c7 \n---------------------------------------------------------------------- \ncom\'è successo?\n----------------------------------------------------------------------', 1691924995),
(32, 'MirandaCosta@gmail.com', '[Contabilità 0000004]: Le fatture sono errate', 'a:3:{s:7:\"headers\";a:2:{s:8:\"keywords\";s:23:\"[Contabilità] Back-End\";s:11:\"In-Reply-To\";s:32:\"9bd1293e3878fb9459d9eba882695fb5\";}s:7:\"charset\";s:5:\"utf-8\";s:8:\"hostname\";s:9:\"localhost\";}', 'The following issue has been CLOSED \n====================================================================== \nhttp://localhost:8989/view.php?id=4 \n====================================================================== \nReported By:                Miranda23\nAssigned To:                Chris95\n====================================================================== \nProject:                    Contabilità\nIssue ID:                   4\nCategory:                   Back-End\nReproducibility:            always\nSeverity:                   major\nPriority:                   high\nStatus:                     closed\nResolution:                 fixed\nFixed in Version:           \n====================================================================== \nDate Submitted:             2023-04-19 13:07 CEST\nLast Modified:              2023-08-13 13:11 CEST\n====================================================================== \nSummary:                    Le fatture sono errate\nDescription: \nLe fatture sono errate\n====================================================================== \n\n---------------------------------------------------------------------- \n (0000007) Miranda23 (updater) - 2023-08-13 13:09\n http://localhost:8989/view.php?id=4#c7 \n---------------------------------------------------------------------- \ncom\'è successo? \n\n---------------------------------------------------------------------- \n (0000008) Chris95 (developer) - 2023-08-13 13:11\n http://localhost:8989/view.php?id=4#c8 \n---------------------------------------------------------------------- \nRisolto \n\nIssue History \nDate Modified    Username       Field                    Change               \n====================================================================== \n2023-04-19 13:07 Miranda23      New Issue                                    \n2023-04-19 13:07 Miranda23      Status                   new => assigned     \n2023-04-19 13:07 Miranda23      Assigned To               => Chris95         \n2023-08-13 13:09 Chris95        Tag Attached: mysql                          \n2023-08-13 13:09 Chris95        Tag Detached: mysql                          \n2023-08-13 13:09 Miranda23      Note Added: 0000007                          \n2023-08-13 13:11 Chris95        Status                   assigned => closed  \n2023-08-13 13:11 Chris95        Resolution               open => fixed       \n2023-08-13 13:11 Chris95        Note Added: 0000008                          \n======================================================================', 1691925062);

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_filters_table`
--

CREATE TABLE `mantis_filters_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `project_id` int(11) NOT NULL DEFAULT '0',
  `is_public` tinyint(4) DEFAULT NULL,
  `name` varchar(64) NOT NULL DEFAULT '',
  `filter_string` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_filters_table`
--

INSERT INTO `mantis_filters_table` (`id`, `user_id`, `project_id`, `is_public`, `name`, `filter_string`) VALUES
(1, 1, -6, 0, '', 'v9#{\"_version\":\"v9\",\"_view_type\":\"simple\",\"category_id\":[\"0\"],\"severity\":[0],\"status\":[0],\"highlight_changed\":6,\"reporter_id\":[0],\"handler_id\":[0],\"project_id\":[-3],\"resolution\":[0],\"build\":[\"0\"],\"version\":[\"0\"],\"hide_status\":[90],\"monitor_user_id\":[0],\"sort\":\"last_updated\",\"dir\":\"DESC\",\"per_page\":50,\"match_type\":0,\"platform\":[\"0\"],\"os\":[\"0\"],\"os_build\":[\"0\"],\"fixed_in_version\":[\"0\"],\"target_version\":[\"0\"],\"profile_id\":[0],\"priority\":[0],\"note_user_id\":[0],\"sticky\":true,\"filter_by_date\":false,\"start_month\":\"04\",\"end_month\":\"04\",\"start_day\":1,\"end_day\":\"19\",\"start_year\":\"2023\",\"end_year\":\"2023\",\"filter_by_last_updated_date\":false,\"last_updated_start_month\":\"04\",\"last_updated_end_month\":\"04\",\"last_updated_start_day\":1,\"last_updated_end_day\":\"19\",\"last_updated_start_year\":\"2023\",\"last_updated_end_year\":\"2023\",\"search\":\"\",\"view_state\":0,\"tag_string\":\"\",\"tag_select\":0,\"relationship_type\":-1,\"relationship_bug\":0,\"custom_fields\":[]}'),
(2, 1, -4, 0, '', 'v9#{\"_version\":\"v9\",\"_view_type\":\"simple\",\"category_id\":[\"0\"],\"severity\":[0],\"status\":[0],\"highlight_changed\":6,\"reporter_id\":[0],\"handler_id\":[0],\"project_id\":[-3],\"resolution\":[0],\"build\":[\"0\"],\"version\":[\"0\"],\"hide_status\":[90],\"monitor_user_id\":[0],\"sort\":\"last_updated\",\"dir\":\"DESC\",\"per_page\":50,\"match_type\":0,\"platform\":[\"0\"],\"os\":[\"0\"],\"os_build\":[\"0\"],\"fixed_in_version\":[\"0\"],\"target_version\":[\"0\"],\"profile_id\":[0],\"priority\":[0],\"note_user_id\":[0],\"sticky\":true,\"filter_by_date\":false,\"start_month\":\"04\",\"end_month\":\"04\",\"start_day\":1,\"end_day\":\"19\",\"start_year\":\"2023\",\"end_year\":\"2023\",\"filter_by_last_updated_date\":false,\"last_updated_start_month\":\"04\",\"last_updated_end_month\":\"04\",\"last_updated_start_day\":1,\"last_updated_end_day\":\"19\",\"last_updated_start_year\":\"2023\",\"last_updated_end_year\":\"2023\",\"search\":\"\",\"view_state\":0,\"tag_string\":\"\",\"tag_select\":0,\"relationship_type\":-1,\"relationship_bug\":0,\"custom_fields\":[]}'),
(3, 1, -3, 0, '', 'v9#{\"_version\":\"v9\",\"_view_type\":\"simple\",\"category_id\":[\"0\"],\"severity\":[0],\"status\":[0],\"per_page\":50,\"highlight_changed\":6,\"reporter_id\":[0],\"handler_id\":[0],\"project_id\":[-3],\"sort\":\"last_updated\",\"dir\":\"DESC\",\"filter_by_date\":false,\"start_month\":8,\"start_day\":1,\"start_year\":2023,\"end_month\":8,\"end_day\":13,\"end_year\":2023,\"filter_by_last_updated_date\":false,\"last_updated_start_month\":8,\"last_updated_start_day\":1,\"last_updated_start_year\":2023,\"last_updated_end_month\":8,\"last_updated_end_day\":13,\"last_updated_end_year\":2023,\"search\":\"\",\"hide_status\":[90],\"resolution\":[0],\"build\":[\"0\"],\"version\":[\"0\"],\"fixed_in_version\":[\"0\"],\"target_version\":[\"0\"],\"priority\":[0],\"monitor_user_id\":[0],\"view_state\":10,\"custom_fields\":[],\"sticky\":true,\"relationship_type\":-1,\"relationship_bug\":0,\"profile_id\":[0],\"platform\":[\"0\"],\"os\":[\"0\"],\"os_build\":[\"0\"],\"tag_string\":\"\",\"tag_select\":0,\"note_user_id\":[0],\"match_type\":0,\"_source_query_id\":7}'),
(4, 3, -5, 0, '', 'v9#{\"_version\":\"v9\",\"_view_type\":\"simple\",\"category_id\":[\"0\"],\"severity\":[0],\"status\":[0],\"highlight_changed\":6,\"reporter_id\":[0],\"handler_id\":[0],\"project_id\":[-3],\"resolution\":[0],\"build\":[\"0\"],\"version\":[\"0\"],\"hide_status\":[90],\"monitor_user_id\":[0],\"sort\":\"last_updated\",\"dir\":\"DESC\",\"per_page\":50,\"match_type\":0,\"platform\":[\"0\"],\"os\":[\"0\"],\"os_build\":[\"0\"],\"fixed_in_version\":[\"0\"],\"target_version\":[\"0\"],\"profile_id\":[0],\"priority\":[0],\"note_user_id\":[0],\"sticky\":true,\"filter_by_date\":false,\"start_month\":\"04\",\"end_month\":\"04\",\"start_day\":1,\"end_day\":\"19\",\"start_year\":\"2023\",\"end_year\":\"2023\",\"filter_by_last_updated_date\":false,\"last_updated_start_month\":\"04\",\"last_updated_end_month\":\"04\",\"last_updated_start_day\":1,\"last_updated_end_day\":\"19\",\"last_updated_start_year\":\"2023\",\"last_updated_end_year\":\"2023\",\"search\":\"\",\"view_state\":0,\"tag_string\":\"\",\"tag_select\":0,\"relationship_type\":-1,\"relationship_bug\":0,\"custom_fields\":[]}'),
(5, 3, -4, 0, '', 'v9#{\"_version\":\"v9\",\"_view_type\":\"simple\",\"category_id\":[\"0\"],\"severity\":[0],\"status\":[0],\"highlight_changed\":6,\"reporter_id\":[0],\"handler_id\":[0],\"project_id\":[-3],\"resolution\":[0],\"build\":[\"0\"],\"version\":[\"0\"],\"hide_status\":[90],\"monitor_user_id\":[0],\"sort\":\"last_updated\",\"dir\":\"DESC\",\"per_page\":50,\"match_type\":0,\"platform\":[\"0\"],\"os\":[\"0\"],\"os_build\":[\"0\"],\"fixed_in_version\":[\"0\"],\"target_version\":[\"0\"],\"profile_id\":[0],\"priority\":[0],\"note_user_id\":[0],\"sticky\":true,\"filter_by_date\":false,\"start_month\":\"04\",\"end_month\":\"04\",\"start_day\":1,\"end_day\":\"19\",\"start_year\":\"2023\",\"end_year\":\"2023\",\"filter_by_last_updated_date\":false,\"last_updated_start_month\":\"04\",\"last_updated_end_month\":\"04\",\"last_updated_start_day\":1,\"last_updated_end_day\":\"19\",\"last_updated_start_year\":\"2023\",\"last_updated_end_year\":\"2023\",\"search\":\"\",\"view_state\":0,\"tag_string\":\"\",\"tag_select\":0,\"relationship_type\":-1,\"relationship_bug\":0,\"custom_fields\":[]}'),
(6, 6, 0, 0, '', 'v9#{\"_version\":\"v9\",\"_view_type\":\"simple\",\"category_id\":[\"0\"],\"severity\":[0],\"status\":[0],\"highlight_changed\":6,\"reporter_id\":[0],\"handler_id\":[0],\"project_id\":[-3],\"resolution\":[0],\"build\":[\"0\"],\"version\":[\"0\"],\"hide_status\":[90],\"monitor_user_id\":[0],\"sort\":\"last_updated\",\"dir\":\"DESC\",\"per_page\":50,\"match_type\":0,\"platform\":[\"0\"],\"os\":[\"0\"],\"os_build\":[\"0\"],\"fixed_in_version\":[\"0\"],\"target_version\":[\"0\"],\"profile_id\":[0],\"priority\":[0],\"note_user_id\":[0],\"sticky\":true,\"filter_by_date\":false,\"start_month\":\"08\",\"end_month\":\"08\",\"start_day\":1,\"end_day\":\"13\",\"start_year\":\"2023\",\"end_year\":\"2023\",\"filter_by_last_updated_date\":false,\"last_updated_start_month\":\"08\",\"last_updated_end_month\":\"08\",\"last_updated_start_day\":1,\"last_updated_end_day\":\"13\",\"last_updated_start_year\":\"2023\",\"last_updated_end_year\":\"2023\",\"search\":\"\",\"view_state\":0,\"tag_string\":\"\",\"tag_select\":0,\"relationship_type\":-1,\"relationship_bug\":0,\"custom_fields\":[]}'),
(7, 1, 0, 1, 'Filtro di prova', 'v9#{\"_version\":\"v9\",\"_view_type\":\"simple\",\"category_id\":[\"0\"],\"severity\":[0],\"status\":[0],\"per_page\":50,\"highlight_changed\":6,\"reporter_id\":[0],\"handler_id\":[0],\"project_id\":[-3],\"sort\":\"last_updated\",\"dir\":\"DESC\",\"filter_by_date\":false,\"start_month\":8,\"start_day\":1,\"start_year\":2023,\"end_month\":8,\"end_day\":13,\"end_year\":2023,\"filter_by_last_updated_date\":false,\"last_updated_start_month\":8,\"last_updated_start_day\":1,\"last_updated_start_year\":2023,\"last_updated_end_month\":8,\"last_updated_end_day\":13,\"last_updated_end_year\":2023,\"search\":\"\",\"hide_status\":[90],\"resolution\":[0],\"build\":[\"0\"],\"version\":[\"0\"],\"fixed_in_version\":[\"0\"],\"target_version\":[\"0\"],\"priority\":[0],\"monitor_user_id\":[0],\"view_state\":10,\"custom_fields\":[],\"sticky\":true,\"relationship_type\":-1,\"relationship_bug\":0,\"profile_id\":[0],\"platform\":[\"0\"],\"os\":[\"0\"],\"os_build\":[\"0\"],\"tag_string\":\"\",\"tag_select\":0,\"note_user_id\":[0],\"match_type\":0}');

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_news_table`
--

CREATE TABLE `mantis_news_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `project_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `poster_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `view_state` smallint(6) NOT NULL DEFAULT '10',
  `announcement` tinyint(4) NOT NULL DEFAULT '0',
  `headline` varchar(64) NOT NULL DEFAULT '',
  `body` longtext NOT NULL,
  `last_modified` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `date_posted` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_plugin_table`
--

CREATE TABLE `mantis_plugin_table` (
  `basename` varchar(40) NOT NULL,
  `enabled` tinyint(4) NOT NULL DEFAULT '0',
  `protected` tinyint(4) NOT NULL DEFAULT '0',
  `priority` int(10) UNSIGNED NOT NULL DEFAULT '3'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_plugin_table`
--

INSERT INTO `mantis_plugin_table` (`basename`, `enabled`, `protected`, `priority`) VALUES
('MantisCoreFormatting', 1, 0, 3);

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_project_file_table`
--

CREATE TABLE `mantis_project_file_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `project_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `title` varchar(250) NOT NULL DEFAULT '',
  `description` varchar(250) NOT NULL DEFAULT '',
  `diskfile` varchar(250) NOT NULL DEFAULT '',
  `filename` varchar(250) NOT NULL DEFAULT '',
  `folder` varchar(250) NOT NULL DEFAULT '',
  `filesize` int(11) NOT NULL DEFAULT '0',
  `file_type` varchar(250) NOT NULL DEFAULT '',
  `content` longblob,
  `date_added` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_project_hierarchy_table`
--

CREATE TABLE `mantis_project_hierarchy_table` (
  `child_id` int(10) UNSIGNED NOT NULL,
  `parent_id` int(10) UNSIGNED NOT NULL,
  `inherit_parent` tinyint(4) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_project_table`
--

CREATE TABLE `mantis_project_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  `status` smallint(6) NOT NULL DEFAULT '10',
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  `view_state` smallint(6) NOT NULL DEFAULT '10',
  `access_min` smallint(6) NOT NULL DEFAULT '10',
  `file_path` varchar(250) NOT NULL DEFAULT '',
  `description` longtext NOT NULL,
  `category_id` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `inherit_global` tinyint(4) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_project_table`
--

INSERT INTO `mantis_project_table` (`id`, `name`, `status`, `enabled`, `view_state`, `access_min`, `file_path`, `description`, `category_id`, `inherit_global`) VALUES
(4, 'Login', 50, 1, 10, 10, '', 'Un sistema di login', 1, 0),
(5, 'Contabilità', 30, 1, 10, 10, '', 'Parte contabile di EasyManager', 1, 1),
(6, 'Fiscalità', 10, 1, 10, 10, '', 'Sezione fisco di EasyManager', 1, 1),
(7, 'Prova1', 10, 1, 50, 10, '', 'Prova', 1, 1),
(8, 'Prova', 10, 1, 10, 10, '', '', 1, 1);

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_project_user_list_table`
--

CREATE TABLE `mantis_project_user_list_table` (
  `project_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `access_level` smallint(6) NOT NULL DEFAULT '10'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_project_version_table`
--

CREATE TABLE `mantis_project_version_table` (
  `id` int(11) NOT NULL,
  `project_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `version` varchar(64) NOT NULL DEFAULT '',
  `description` longtext NOT NULL,
  `released` tinyint(4) NOT NULL DEFAULT '1',
  `obsolete` tinyint(4) NOT NULL DEFAULT '0',
  `date_order` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_project_version_table`
--

INSERT INTO `mantis_project_version_table` (`id`, `project_id`, `version`, `description`, `released`, `obsolete`, `date_order`) VALUES
(3, 7, '1.0', '', 0, 0, 1691925000);

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_sponsorship_table`
--

CREATE TABLE `mantis_sponsorship_table` (
  `id` int(11) NOT NULL,
  `bug_id` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `amount` int(11) NOT NULL DEFAULT '0',
  `logo` varchar(128) NOT NULL DEFAULT '',
  `url` varchar(128) NOT NULL DEFAULT '',
  `paid` tinyint(4) NOT NULL DEFAULT '0',
  `date_submitted` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `last_updated` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_tag_table`
--

CREATE TABLE `mantis_tag_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `name` varchar(100) NOT NULL DEFAULT '',
  `description` longtext NOT NULL,
  `date_created` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `date_updated` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_tag_table`
--

INSERT INTO `mantis_tag_table` (`id`, `user_id`, `name`, `description`, `date_created`, `date_updated`) VALUES
(1, 1, 'testing', '', 1681901937, 1681901937),
(2, 1, 'mysql', '', 1681901943, 1681901943);

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_tokens_table`
--

CREATE TABLE `mantis_tokens_table` (
  `id` int(11) NOT NULL,
  `owner` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `value` longtext NOT NULL,
  `timestamp` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `expiry` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_tokens_table`
--

INSERT INTO `mantis_tokens_table` (`id`, `owner`, `type`, `value`, `timestamp`, `expiry`) VALUES
(33, 1, 4, '1', 1697798903, 1697799215);

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_user_pref_table`
--

CREATE TABLE `mantis_user_pref_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `project_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `default_profile` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `default_project` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `refresh_delay` int(11) NOT NULL DEFAULT '0',
  `redirect_delay` int(11) NOT NULL DEFAULT '0',
  `bugnote_order` varchar(4) NOT NULL DEFAULT 'ASC',
  `email_on_new` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_assigned` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_feedback` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_resolved` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_closed` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_reopened` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_bugnote` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_status` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_priority` tinyint(4) NOT NULL DEFAULT '0',
  `email_on_priority_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_status_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_bugnote_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_reopened_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_closed_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_resolved_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_feedback_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_assigned_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_on_new_min_severity` smallint(6) NOT NULL DEFAULT '10',
  `email_bugnote_limit` smallint(6) NOT NULL DEFAULT '0',
  `language` varchar(32) NOT NULL DEFAULT 'english',
  `timezone` varchar(32) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_user_pref_table`
--

INSERT INTO `mantis_user_pref_table` (`id`, `user_id`, `project_id`, `default_profile`, `default_project`, `refresh_delay`, `redirect_delay`, `bugnote_order`, `email_on_new`, `email_on_assigned`, `email_on_feedback`, `email_on_resolved`, `email_on_closed`, `email_on_reopened`, `email_on_bugnote`, `email_on_status`, `email_on_priority`, `email_on_priority_min_severity`, `email_on_status_min_severity`, `email_on_bugnote_min_severity`, `email_on_reopened_min_severity`, `email_on_closed_min_severity`, `email_on_resolved_min_severity`, `email_on_feedback_min_severity`, `email_on_assigned_min_severity`, `email_on_new_min_severity`, `email_bugnote_limit`, `language`, `timezone`) VALUES
(1, 1, 0, 0, 3, 30, 2, 'ASC', 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 'auto', 'Europe/Berlin');

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_user_print_pref_table`
--

CREATE TABLE `mantis_user_print_pref_table` (
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `print_pref` varchar(64) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_user_profile_table`
--

CREATE TABLE `mantis_user_profile_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL DEFAULT '0',
  `platform` varchar(32) NOT NULL DEFAULT '',
  `os` varchar(32) NOT NULL DEFAULT '',
  `os_build` varchar(32) NOT NULL DEFAULT '',
  `description` longtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_user_profile_table`
--

INSERT INTO `mantis_user_profile_table` (`id`, `user_id`, `platform`, `os`, `os_build`, `description`) VALUES
(1, 0, 'PC', 'Windows', '10', '');

-- --------------------------------------------------------

--
-- Struttura della tabella `mantis_user_table`
--

CREATE TABLE `mantis_user_table` (
  `id` int(10) UNSIGNED NOT NULL,
  `username` varchar(191) NOT NULL DEFAULT '',
  `realname` varchar(191) NOT NULL DEFAULT '',
  `email` varchar(191) NOT NULL DEFAULT '',
  `password` varchar(64) NOT NULL DEFAULT '',
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  `protected` tinyint(4) NOT NULL DEFAULT '0',
  `access_level` smallint(6) NOT NULL DEFAULT '10',
  `login_count` int(11) NOT NULL DEFAULT '0',
  `lost_password_request_count` smallint(6) NOT NULL DEFAULT '0',
  `failed_login_count` smallint(6) NOT NULL DEFAULT '0',
  `cookie_string` varchar(64) NOT NULL DEFAULT '',
  `last_visit` int(10) UNSIGNED NOT NULL DEFAULT '1',
  `date_created` int(10) UNSIGNED NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dump dei dati per la tabella `mantis_user_table`
--

INSERT INTO `mantis_user_table` (`id`, `username`, `realname`, `email`, `password`, `enabled`, `protected`, `access_level`, `login_count`, `lost_password_request_count`, `failed_login_count`, `cookie_string`, `last_visit`, `date_created`) VALUES
(1, 'administrator', '', 'root@localhost', '63a9f0ea7bb98050796b649e85481845', 1, 0, 90, 25, 0, 0, '66c20bc17ca1782cf93f4670d854e1a853ce8fd7c6aafc55d6511d21a5005618', 1697798915, 1681813688),
(2, 'Mario', 'Mario Rossi', 'mariorossi43256@gmail.com', '63a9f0ea7bb98050796b649e85481845', 1, 0, 25, 2, 0, 0, 'jLqIyNSbhv-6sA_IqKlbS717yACVaPPEgfGqEXD9n3IwUUo6WgETwof15_9jQ_C_', 1681902350, 1681813739),
(3, 'Ivan52', 'Ivan Capasso', 'noctino.misterioso@gmail.com', '63a9f0ea7bb98050796b649e85481845', 1, 0, 25, 2, 0, 0, '3z1PLAItZqdMBG517MLm6hXD_gEOHb__-djJj81jFUQ-CTtp6R77c1Hw9SQr2ECK', 1681920545, 1681814009),
(4, 'Lory', 'Lorenzo Monaldo', 'LorenzoMonaldo@gmail.com', '63a9f0ea7bb98050796b649e85481845', 1, 0, 10, 1, 0, 0, '6VHt4bPwEVe3I8AebpfZbxXeZzRRW_kY4Xr34wCVa0_flHVbBvT7cKzriDdBC8sW', 1691925029, 1681894261),
(5, 'Miranda23', 'Miranda Costa', 'MirandaCosta@gmail.com', '63a9f0ea7bb98050796b649e85481845', 1, 0, 40, 2, 0, 0, '4PdI8wAqyMe9bvmodlw6KwWk1lDZfxwhvufeo3DWQ_GbL-6521s6tPSzP9CgpiFH', 1691924996, 1681894353),
(6, 'Chris95', 'Cristiano Russo', 'CristianoRusso@gmail.com', '63a9f0ea7bb98050796b649e85481845', 1, 0, 55, 7, 0, 0, '5BREKYrYQBjSjdo7UJAbuecBjEO5WcLspXe0YvQozKSf-UBzkXG-PxIQUFLeeyzB', 1691925062, 1681894419),
(7, 'Tiziano28', 'Tiziano Conti', 'TizianoConti@gmail.com', '63a9f0ea7bb98050796b649e85481845\r\n', 1, 0, 90, 0, 0, 6, 'JaUSD9THxCm5HhggOjkU4YEuSD9jgJcKTcE_AlmSkFs0h9l3v4nVPwPfL_An81br', 1681894476, 1681894476);

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `mantis_api_token_table`
--
ALTER TABLE `mantis_api_token_table`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_user_id_name` (`user_id`,`name`);

--
-- Indici per le tabelle `mantis_bugnote_table`
--
ALTER TABLE `mantis_bugnote_table`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_bug` (`bug_id`),
  ADD KEY `idx_last_mod` (`last_modified`);

--
-- Indici per le tabelle `mantis_bugnote_text_table`
--
ALTER TABLE `mantis_bugnote_text_table`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `mantis_bug_file_table`
--
ALTER TABLE `mantis_bug_file_table`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_bug_file_bug_id` (`bug_id`),
  ADD KEY `idx_diskfile` (`diskfile`);

--
-- Indici per le tabelle `mantis_bug_history_table`
--
ALTER TABLE `mantis_bug_history_table`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_bug_history_bug_id` (`bug_id`),
  ADD KEY `idx_history_user_id` (`user_id`),
  ADD KEY `idx_bug_history_date_modified` (`date_modified`);

--
-- Indici per le tabelle `mantis_bug_monitor_table`
--
ALTER TABLE `mantis_bug_monitor_table`
  ADD PRIMARY KEY (`user_id`,`bug_id`),
  ADD KEY `idx_bug_id` (`bug_id`);

--
-- Indici per le tabelle `mantis_bug_relationship_table`
--
ALTER TABLE `mantis_bug_relationship_table`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_relationship_source` (`source_bug_id`),
  ADD KEY `idx_relationship_destination` (`destination_bug_id`);

--
-- Indici per le tabelle `mantis_bug_revision_table`
--
ALTER TABLE `mantis_bug_revision_table`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_bug_rev_type` (`type`),
  ADD KEY `idx_bug_rev_id_time` (`bug_id`,`timestamp`);

--
-- Indici per le tabelle `mantis_bug_table`
--
ALTER TABLE `mantis_bug_table`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_bug_sponsorship_total` (`sponsorship_total`),
  ADD KEY `idx_bug_fixed_in_version` (`fixed_in_version`),
  ADD KEY `idx_bug_status` (`status`),
  ADD KEY `idx_project` (`project_id`);

--
-- Indici per le tabelle `mantis_bug_tag_table`
--
ALTER TABLE `mantis_bug_tag_table`
  ADD PRIMARY KEY (`bug_id`,`tag_id`),
  ADD KEY `idx_bug_tag_tag_id` (`tag_id`);

--
-- Indici per le tabelle `mantis_bug_text_table`
--
ALTER TABLE `mantis_bug_text_table`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `mantis_category_table`
--
ALTER TABLE `mantis_category_table`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_category_project_name` (`project_id`,`name`);

--
-- Indici per le tabelle `mantis_config_table`
--
ALTER TABLE `mantis_config_table`
  ADD PRIMARY KEY (`config_id`,`project_id`,`user_id`);

--
-- Indici per le tabelle `mantis_custom_field_project_table`
--
ALTER TABLE `mantis_custom_field_project_table`
  ADD PRIMARY KEY (`field_id`,`project_id`);

--
-- Indici per le tabelle `mantis_custom_field_string_table`
--
ALTER TABLE `mantis_custom_field_string_table`
  ADD PRIMARY KEY (`field_id`,`bug_id`),
  ADD KEY `idx_custom_field_bug` (`bug_id`);

--
-- Indici per le tabelle `mantis_custom_field_table`
--
ALTER TABLE `mantis_custom_field_table`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_custom_field_name` (`name`);

--
-- Indici per le tabelle `mantis_email_table`
--
ALTER TABLE `mantis_email_table`
  ADD PRIMARY KEY (`email_id`);

--
-- Indici per le tabelle `mantis_filters_table`
--
ALTER TABLE `mantis_filters_table`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `mantis_news_table`
--
ALTER TABLE `mantis_news_table`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `mantis_plugin_table`
--
ALTER TABLE `mantis_plugin_table`
  ADD PRIMARY KEY (`basename`);

--
-- Indici per le tabelle `mantis_project_file_table`
--
ALTER TABLE `mantis_project_file_table`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `mantis_project_hierarchy_table`
--
ALTER TABLE `mantis_project_hierarchy_table`
  ADD UNIQUE KEY `idx_project_hierarchy` (`child_id`,`parent_id`),
  ADD KEY `idx_project_hierarchy_child_id` (`child_id`),
  ADD KEY `idx_project_hierarchy_parent_id` (`parent_id`);

--
-- Indici per le tabelle `mantis_project_table`
--
ALTER TABLE `mantis_project_table`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_project_name` (`name`),
  ADD KEY `idx_project_view` (`view_state`);

--
-- Indici per le tabelle `mantis_project_user_list_table`
--
ALTER TABLE `mantis_project_user_list_table`
  ADD PRIMARY KEY (`project_id`,`user_id`),
  ADD KEY `idx_project_user` (`user_id`);

--
-- Indici per le tabelle `mantis_project_version_table`
--
ALTER TABLE `mantis_project_version_table`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_project_version` (`project_id`,`version`);

--
-- Indici per le tabelle `mantis_sponsorship_table`
--
ALTER TABLE `mantis_sponsorship_table`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_sponsorship_bug_id` (`bug_id`),
  ADD KEY `idx_sponsorship_user_id` (`user_id`);

--
-- Indici per le tabelle `mantis_tag_table`
--
ALTER TABLE `mantis_tag_table`
  ADD PRIMARY KEY (`id`,`name`),
  ADD KEY `idx_tag_name` (`name`);

--
-- Indici per le tabelle `mantis_tokens_table`
--
ALTER TABLE `mantis_tokens_table`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_typeowner` (`type`,`owner`);

--
-- Indici per le tabelle `mantis_user_pref_table`
--
ALTER TABLE `mantis_user_pref_table`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `mantis_user_print_pref_table`
--
ALTER TABLE `mantis_user_print_pref_table`
  ADD PRIMARY KEY (`user_id`);

--
-- Indici per le tabelle `mantis_user_profile_table`
--
ALTER TABLE `mantis_user_profile_table`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `mantis_user_table`
--
ALTER TABLE `mantis_user_table`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `idx_user_cookie_string` (`cookie_string`),
  ADD UNIQUE KEY `idx_user_username` (`username`),
  ADD KEY `idx_enable` (`enabled`),
  ADD KEY `idx_access` (`access_level`),
  ADD KEY `idx_email` (`email`);

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `mantis_api_token_table`
--
ALTER TABLE `mantis_api_token_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `mantis_bugnote_table`
--
ALTER TABLE `mantis_bugnote_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `mantis_bugnote_text_table`
--
ALTER TABLE `mantis_bugnote_text_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `mantis_bug_file_table`
--
ALTER TABLE `mantis_bug_file_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `mantis_bug_history_table`
--
ALTER TABLE `mantis_bug_history_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=62;

--
-- AUTO_INCREMENT per la tabella `mantis_bug_relationship_table`
--
ALTER TABLE `mantis_bug_relationship_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `mantis_bug_revision_table`
--
ALTER TABLE `mantis_bug_revision_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `mantis_bug_table`
--
ALTER TABLE `mantis_bug_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT per la tabella `mantis_bug_text_table`
--
ALTER TABLE `mantis_bug_text_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT per la tabella `mantis_category_table`
--
ALTER TABLE `mantis_category_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT per la tabella `mantis_custom_field_table`
--
ALTER TABLE `mantis_custom_field_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `mantis_email_table`
--
ALTER TABLE `mantis_email_table`
  MODIFY `email_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT per la tabella `mantis_filters_table`
--
ALTER TABLE `mantis_filters_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT per la tabella `mantis_news_table`
--
ALTER TABLE `mantis_news_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `mantis_project_file_table`
--
ALTER TABLE `mantis_project_file_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `mantis_project_table`
--
ALTER TABLE `mantis_project_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT per la tabella `mantis_project_version_table`
--
ALTER TABLE `mantis_project_version_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT per la tabella `mantis_sponsorship_table`
--
ALTER TABLE `mantis_sponsorship_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `mantis_tag_table`
--
ALTER TABLE `mantis_tag_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT per la tabella `mantis_tokens_table`
--
ALTER TABLE `mantis_tokens_table`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT per la tabella `mantis_user_pref_table`
--
ALTER TABLE `mantis_user_pref_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT per la tabella `mantis_user_profile_table`
--
ALTER TABLE `mantis_user_profile_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT per la tabella `mantis_user_table`
--
ALTER TABLE `mantis_user_table`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
