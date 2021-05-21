import argparse, logging, re
from googleapiclient.discovery import build
from oauth2client.client import GoogleCredentials


def retrieve_job_id():
    project = 'staging-btc-etl'
    job_prefix = "tigergraph"
    location = 'us-central1'

    logging.info("Looking for jobs with prefix {} in region {}...".format(job_prefix, location))

    try:
        credentials = GoogleCredentials.get_application_default()
        dataflow = build('dataflow', 'v1b3', credentials=credentials)

        result = dataflow.projects().locations().jobs().list(
            projectId=project,
            location=location,
        ).execute()


        #print(result)

        for job in result['jobs']:
            if job_prefix in job['name']:
                job_id = job['id']
                #print(job['name'])
                print("gcloud dataflow jobs drain --region us-central1 " + job_id)

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
    retrieve_job_id()