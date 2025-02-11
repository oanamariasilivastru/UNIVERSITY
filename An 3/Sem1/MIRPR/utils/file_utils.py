import os


def get_file_name(current_time, output_dir, model_name, extension):
    formatted_time = current_time.strftime("%d-%m-%Y_%H-%M-%S")
    trained_model_file = os.path.join(output_dir, formatted_time + "_" + str(model_name) + "." + extension)
    return trained_model_file
