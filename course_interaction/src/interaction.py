import sys
import os

basePath = os.path.dirname(os.path.realpath(__file__))
sys.path.append(os.path.join(basePath, 'tools'))
from tools.basic_module import *
import tools.core_funs as core_funs
import tools.util_tools as util_tools

def interaction(text, task_id=''):
    logger.info('task_id:{}'.format(task_id))
    interaction_level = -1
    error_code, error_message, df = util_tools.parse_list_2_df(text, task_id)
    if error_code == success:
        interaction_level = core_funs.get_interaction_score(df, task_id)
    return {'error_code': error_code, 'error_message': error_message, 'interaction_level': interaction_level}

