# utils/eval_utils.py

import numpy as np

def intersect(box_a, box_b):
    max_xy = np.minimum(box_a[:, 2:], box_b[2:])
    min_xy = np.maximum(box_a[:, :2], box_b[:2])
    inter = np.clip((max_xy - min_xy), a_min=0, a_max=np.inf)
    return inter[:, 0] * inter[:, 1]

def jaccard_numpy(box_a, box_b):
    """Compute the jaccard overlap of two sets of boxes."""
    inter = intersect(box_a, box_b)
    area_a = ((box_a[:, 2] - box_a[:, 0]) *
              (box_a[:, 3] - box_a[:, 1]))  # [A,B]
    area_b = ((box_b[:, 2] - box_b[:, 0]) *
              (box_b[:, 3] - box_b[:, 1]))  # [A,B]
    union = area_a[:, np.newaxis] + area_b - inter
    return inter / union  # [A,B]

def voc_ap(rec, prec):
    """Compute VOC AP given precision and recall."""
    rec = np.concatenate(([0.0], rec, [1.0]))
    prec = np.concatenate(([0.0], prec, [0.0]))
    mrec = np.maximum.accumulate(rec[::-1])[::-1]
    mpre = np.maximum.accumulate(prec[::-1])[::-1]
    i_list = np.where(mrec[1:] != mrec[:-1])[0] + 1
    ap = 0.0
    for i in i_list:
        ap += ((mrec[i] - mrec[i - 1]) * mpre[i])
    return ap, mrec, mpre

def get_mAP(pred_data, gt_data, overlap_threshold=0.5):
    """Calculate mean Average Precision (mAP) given predictions and ground truths."""
    gt_counter_per_class = {}
    out_gt_data = {}
    for image_id in gt_data.keys():
        out_gt_data[image_id] = []
        for item in gt_data[image_id]:
            label = item['label']
            out_gt_data[image_id].append({'box': item['box'], 'label': label, 'used': False})
            if label in gt_counter_per_class:
                gt_counter_per_class[label] += 1
            else:
                gt_counter_per_class[label] = 1

    gt_data = out_gt_data
    gt_classes = sorted(gt_counter_per_class.keys())
    n_classes = len(gt_classes)

    detect_results = {}
    for label in gt_classes:
        bounding_boxes = []
        for image_id in pred_data.keys():
            for item in pred_data[image_id]:
                if item['label'] == label:
                    bounding_boxes.append({'box': item['box'], 'image_id': image_id, 'score': item['score']})
        bounding_boxes.sort(key=lambda x: float(x['score']), reverse=True)
        detect_results[label] = bounding_boxes

    sum_AP = 0.0
    count_true_positives = {}

    for label in gt_classes:
        count_true_positives[label] = 0
        dr_data = detect_results[label]

        nd = len(dr_data)
        tp = np.zeros(nd)
        fp = np.zeros(nd)

        for idx, detection in enumerate(dr_data):
            image_id = detection['image_id']
            gt_objects = gt_data.get(image_id, [])
            bb = np.array(detection['box']).astype(float)
            ovmax = -1
            gt_match = -1
            for obj_idx, obj in enumerate(gt_objects):
                if obj['label'] != label:
                    continue
                bbgt = np.array(obj['box']).astype(float)
                inter = intersect(np.array([bb]), bbgt[np.newaxis, :])[0]
                area_gt = (bbgt[2] - bbgt[0]) * (bbgt[3] - bbgt[1])
                area_det = (bb[2] - bb[0]) * (bb[3] - bb[1])
                union = area_gt + area_det - inter
                overlap = inter / union if union > 0 else 0
                if overlap > ovmax:
                    ovmax = overlap
                    gt_match = obj_idx

            min_overlap = overlap_threshold
            if ovmax >= min_overlap:
                if not gt_objects[gt_match]['used']:
                    tp[idx] = 1
                    gt_objects[gt_match]['used'] = True
                    count_true_positives[label] += 1
                else:
                    fp[idx] = 1
            else:
                fp[idx] = 1

        fp = np.cumsum(fp)
        tp = np.cumsum(tp)
        rec = tp / gt_counter_per_class[label]
        prec = tp / (tp + fp + 1e-16)
        ap, _, _ = voc_ap(rec.tolist(), prec.tolist())
        sum_AP += ap

    mAP = sum_AP / n_classes
    return mAP
