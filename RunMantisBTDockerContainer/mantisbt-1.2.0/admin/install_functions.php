<?php
# MantisBT - a php based bugtracking system

# MantisBT is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 2 of the License, or
# (at your option) any later version.
#
# MantisBT is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with MantisBT.  If not, see <http://www.gnu.org/licenses/>.

/**
 * Update functions for the installation schema's 'UpdateFunction' option.
 * All functions must be name install_<function_name> and referenced as just <function_name>.
 * @package MantisBT
 * @copyright Copyright (C) 2000 - 2002  Kenzaburo Ito - kenito@300baud.org
 * @copyright Copyright (C) 2002 - 2010  MantisBT Team - mantisbt-dev@lists.sourceforge.net
 * @link http://www.mantisbt.org
 */

/**
 * Migrate the legacy category data to the new category_id-based schema.
 */
function install_category_migrate() {
	global $g_db_log_queries;
	
	$t_bug_table = db_get_table( 'mantis_bug_table' );
	$t_category_table = db_get_table( 'mantis_category_table' );
	$t_project_category_table = db_get_table( 'mantis_project_category_table' );

	// disable query logging (even if it's enabled in config for this)
	if ( $g_db_log_queries !== 0 ) {
		$t_log_queries = $g_db_log_queries;
		$g_db_log_queries = 0;
	} else {
		$t_log_queries = null;
	}

	$query = "SELECT project_id, category FROM $t_project_category_table ORDER BY project_id, category";
	$t_category_result = db_query_bound( $query );

	$query = "SELECT project_id, category FROM $t_bug_table ORDER BY project_id, category";
	$t_bug_result = db_query_bound( $query );

	$t_data = Array();

	# Find categories specified by project
	while( $row = db_fetch_array( $t_category_result ) ) {
		$t_project_id = $row['project_id'];
		$t_name = $row['category'];
		$t_data[$t_project_id][$t_name] = true;
	}

	# Find orphaned categories from bugs
	while( $row = db_fetch_array( $t_bug_result ) ) {
		$t_project_id = $row['project_id'];
		$t_name = $row['category'];

		$t_data[$t_project_id][$t_name] = true;
	}

	# In every project, go through all the categories found, and create them and update the bug
	foreach( $t_data as $t_project_id => $t_categories ) {
		$t_inserted = array();
		foreach( $t_categories as $t_name => $t_true ) {
			$t_lower_name = utf8_strtolower( trim( $t_name ) );
			if ( !isset( $t_inserted[$t_lower_name] ) ) {
				$query = "INSERT INTO $t_category_table ( name, project_id ) VALUES ( " . db_param() . ', ' . db_param() . ' )';
				db_query_bound( $query, array( $t_name, $t_project_id ) );
				$t_category_id = db_insert_id( $t_category_table );
				$t_inserted[$t_lower_name] = $t_category_id;
			} else {
				$t_category_id = $t_inserted[$t_lower_name];
			}

			$query = "UPDATE $t_bug_table SET category_id=" . db_param() . '
						WHERE project_id=' . db_param() . ' AND category=' . db_param();
			db_query_bound( $query, array( $t_category_id, $t_project_id, $t_name ) );
		}
	}

	// re-enabled query logging if we disabled it
	if ( $t_log_queries !== null ) {
		$g_db_log_queries = $t_log_queries;
	}

	# return 2 because that's what ADOdb/DataDict does when things happen properly
	return 2;
}

function install_date_migrate( $p_data) {
	// $p_data[0] = tablename, [1] id column, [2] = old column, [3] = new column
	global $g_db_log_queries;
	
	// disable query logging (even if it's enabled in config for this)
	if ( $g_db_log_queries !== 0 ) {
		$t_log_queries = $g_db_log_queries;
		$g_db_log_queries = 0;
	} else {
		$t_log_queries = null;
	}

	$t_table = db_get_table( $p_data[0] );
	$t_id_column = $p_data[1];

	if ( is_array( $p_data[2] ) ) {
		$t_old_column = implode( ',', $p_data[2] );
		$t_date_array = true;
		$t_cnt_fields = sizeof( $p_data[2] );
		$t_pairs = array();
		foreach( $p_data[3] as $var ) {
			array_push( $t_pairs, "$var=" . db_param() ) ;
		}		
		$t_new_column = implode( ',', $t_pairs );
	} else {
		$t_old_column = $p_data[2];
		$t_new_column = $p_data[3] . "=" . db_param();
		$t_date_array = false;
	}
	
	$query = "SELECT $t_id_column, $t_old_column FROM $t_table";
	$t_result = db_query_bound( $query );

	while( $row = db_fetch_array( $t_result ) ) {
		$t_id = (int)$row[$t_id_column];

		if( $t_date_array ) {
			for( $i=0; $i < $t_cnt_fields; $i++ ) {
				$t_old_value = $row[$p_data[2][$i]];

				$t_new_value[$i] = db_unixtimestamp($t_old_value);
				if ($t_new_value[$i] < 100000 ) {
					$t_new_value[$i] = 1;
				}
			
			}
			$t_values = $t_new_value;
			$t_values[] = $t_id;
		} else {
			$t_old_value = $row[$t_old_column];		

			$t_new_value = db_unixtimestamp($t_old_value);
			if ($t_new_value < 100000 ) {
				$t_new_value = 1;
			}
			$t_values = array( $t_new_value, $t_id);
		}
		
		$query = "UPDATE $t_table SET $t_new_column
					WHERE $t_id_column=" . db_param();
		db_query_bound( $query, $t_values );
	}

	// re-enabled query logging if we disabled it
	if ( $t_log_queries !== null ) {
		$g_db_log_queries = $t_log_queries;
	}

	# return 2 because that's what ADOdb/DataDict does when things happen properly
	return 2;	

}
