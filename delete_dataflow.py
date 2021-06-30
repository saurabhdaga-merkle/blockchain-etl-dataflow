import sys, argparse, logging, re
import readline
import shlex
from googleapiclient.discovery import build
from oauth2client.client import GoogleCredentials


def retrieve_job_id(job_prefix):
    project = 'staging-btc-etl'
    job_prefix = job_prefix
    location = 'us-central1'

    commands = []
    logging.info("Looking for jxxxobs with prefix {} in region {}...".format(job_prefix, location))

    try:
        credentials = GoogleCredentials.get_application_default()
        dataflow = build('dataflow', 'v1b3', credentials=credentials)

        result = dataflow.projects().locations().jobs().list(
            projectId=project,
            location=location,
        ).execute()
        print(job_prefix)
        for job in result['jobs']:
            if job['name'].find(job_prefix) != -1 and (
                    ('DRAINED') not in job['currentState'].find('DRAINED') == 0 or
                    ('CANCELLED') not in job['currentState']
            ):
                job_id = job['id']
                print(f"Are you sure you want to drain this - Drain {job}?(Y/N)")
                cmd = shlex.split(input('> '))
                print(cmd)
                if (cmd[0].lower() == "y"):
                    commands.append("gcloud dataflow jobs drain --region us-central1 " + job_id)

        for item in commands:
            print(item)

    except Exception as e:
        logging.info("Error retrieving Job ID")
        raise KeyError(e)


def run(argv=None):
    parser = argparse.ArgumentParser()
    known_args, pipeline_args = parser.parse_known_args(argv)

    pipeline_options = PipelineOptions(pipeline_args)
    pipeline_options.view_as(SetupOptions).save_main_session = True

    p = beam.Pipeline(options=pipeline_options)

    init_data = (p
                 | 'Start' >> beam.Create(["Init pipeline"])
                 | 'Retrieve Job ID' >> beam.FlatMap(retrieve_job_id))

    p.run()


if __name__ == '__main__':
    retrieve_job_id(sys.argv[1])